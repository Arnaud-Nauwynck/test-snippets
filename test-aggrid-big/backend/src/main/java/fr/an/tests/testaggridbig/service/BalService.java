package fr.an.tests.testaggridbig.service;

import com.opencsv.CSVParserBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import de.siegmar.fastcsv.reader.CommentStrategy;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import de.siegmar.fastcsv.reader.NamedCsvRecord;
import fr.an.tests.testaggridbig.dto.*;
import fr.an.tests.testaggridbig.util.FileChunkMultiThreadedFastCsvReader;
import fr.an.tests.testaggridbig.util.FileChunkMultiThreadedOpenCsvReader;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Service
@Slf4j
public class BalService {

    @Getter
    private boolean dataLoaded;

    @Value("${app.opendata.bal.format:multiCsvGzFiles}")
    private String balFormat;

    @Value("${app.opendata.bal.path}")
    private String openDataBalPath;

    @Value("${app.opendata.bal.multiCsvGzPath}")
    private String openDataBalMultiCsvGzPath;

    @Value("${app.opendata.bal.filterDepts}")
    private String filterDepts;
    private Set<Integer> filterDeptsSet;

    @Value("${app.csv.lib:fastcsv}") // TODO "chunk-fastcsv", "fastcsv", "opencsv", "chunk-opencsv"
    private String csvLib;

    @Value("${app.opendata.bal.enableDetailedAddresses:true}")
    private boolean enableDetailedAddresses;

    @Getter
    private List<CityDTO> cities = new ArrayList<>();
    private Map<String, CityDTO> cityByZipCode = new HashMap<>();

    public List<CityStreetDTO> getStreets() {
        val res = new ArrayList<CityStreetDTO>(1000000);
        for(val city: cities) {
            res.addAll(city.streets);
        }
        return res;
    }

    @Getter
    private List<StreetNameDTO> streetNames = new ArrayList<>();
    private Map<String, StreetNameDTO> streetByName = new HashMap<>();

    @Getter
    private List<AddressDTO> addresses = new ArrayList<>();

    private int currentFilteredAddresses = 0;
    private int currentFilteredFiles = 0;
    private int currentLoadedAddresses = 0;
    private int freqProgressLoadedAddresss = 500_000;
    private int progressNextDisplayLoadedAddresses = freqProgressLoadedAddresss;

    @Getter
    private BalSummaryDTO balSummary;

    //---------------------------------------------------------------------------------------------

    /**
     * Filter addresses with dept in [75, 91, 92, 77, 93, 78, 94, 95]
     * using FastCSV =>
     * <PRE>
     * done loaded all addresses, took 240251 ms, summary:{cityCount=1287, streetNameCount=70461, cityStreetCount=118618, addressCount=2208647}, filteredAddresses:24007114
     * </PRE>
     */
    @PostConstruct
    public void init() {
        log.info("init -> async load all addresses");

        filterDeptsSet = new HashSet();
        for (String dept : filterDepts.split(",")) {
            filterDeptsSet.add(Integer.parseInt(dept));
        }
        log.info("Filter addresses with dept in " + filterDeptsSet);

        new Thread(() -> {
            log.info("load all addresses");
            long start = System.currentTimeMillis();
            try {
                loadData();
            } catch(IOException ex) {
                throw new RuntimeException("Failed to read", ex);
            }
            this.dataLoaded = true;
            int millis = (int) (System.currentTimeMillis() - start);
            log.info("done loaded all addresses, took " + millis + " ms,"
                    + " summary:" + balSummary
                    + ", filteredAddresses:" + currentFilteredAddresses);
        }).start();
    }

    private void loadData() throws IOException {
        if (balFormat.equals("multiCsvGzFiles")) {
            Path dir = Paths.get(openDataBalMultiCsvGzPath);
            log.info("read OpenData BAL ~100 x files .csv.gz using ThreadPool");
            ExecutorService execService = ForkJoinPool.commonPool();
            val beansFutures = new ArrayList<Future<List<AddressCsvBean>>>();
            Files.list(dir).forEach(filePath -> {
                String fileName = filePath.getFileName().toString();
                if (fileName.startsWith("adresses-") && fileName.endsWith(".csv.gz")) {
                    String deptText = fileName.substring("adresses-".length(), fileName.length() - ".csv.gz".length());
                    int dept = Integer.parseInt(deptText);
                    if (filterDeptsSet.contains(dept)) {
                        beansFutures.add(execService.submit(() -> readCsvGzAddresses(filePath)));
                    } else {
                        log.info("ignore dept file name: '" + fileName + "'");
                        currentFilteredFiles++;
                    }
                } else {
                    log.info("ignore unrecognized file name: '" + fileName + "'");
                    currentFilteredFiles++;
                }
            });
            for(val beansFuture: beansFutures) {
                try {
                    List<AddressCsvBean> addressBeans = beansFuture.get();
                    registerAddresses(addressBeans);

                    addressBeans.clear(); // help gc
                } catch (InterruptedException | ExecutionException ex) {
                    log.error("Failed to read file .. ignore", ex);
                    // ignore, no rethrow!
                }
            }
        } else {
            log.info("read OpenData BAL single csv file");
            Path filePath = Paths.get(openDataBalPath);
            if (csvLib.equals("opencsv")) {
                readAndRegisterAddressesUsingOpenCsv(filePath);
            } else if (csvLib.equals("chunk-opencsv")) { // TODO does not work yet
                readAndRegisterAddressesUsingChunkThreadsOpenCsv(filePath);
            } else if (csvLib.equals("fastcsv")) {
                readAndRegisterAddressesUsingFastCsv(filePath);
            } else if (csvLib.equals("chunk-fastcsv")) { // TODO does not work yet
                readAndRegisterAddressesUsingChunkThreadsFastCsv(filePath);
            } else {
                throw new RuntimeException();
            }
        }

        // finish, compute avg coord from temporary sum
        for(val city: cities) {
            city.coordSet.getAvgCoord();
        }
        // TOADD... compute address coord relative to city coord?

        int cityStreetCount = 0;
        for(val city: cities) {
            cityStreetCount += city.streetById.size();
        }
        this.balSummary = new BalSummaryDTO(
                cities.size(), streetNames.size(), cityStreetCount, addresses.size()
        );
    }

    private List<AddressCsvBean> readCsvGzAddresses(Path filePath) {
        List<AddressCsvBean> res;
        long startTime = System.currentTimeMillis();
        try (Reader reader = new BufferedReader(new InputStreamReader(
                new GZIPInputStream(
                        new BufferedInputStream(new FileInputStream(filePath.toString()))
                )))
        ) {
            if (csvLib.equals("opencsv")) {
                res = readCsvGzAddressesUsingOpenCsv(filePath, reader);
            } else if (csvLib.equals("fastcsv")) {
                res = readCsvGzAddressesUsingFastCsv(filePath, reader);
            } else {
                throw new IllegalArgumentException();
            }
            val millis = System.currentTimeMillis() - startTime;
            log.info("loaded file " + filePath.getFileName() + ", took " + millis + " ms, got " + res.size() + " rows");
            return res;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read file " + filePath, ex);
        }
    }

    private List<AddressCsvBean> readCsvGzAddressesUsingOpenCsv(Path filePath, Reader reader) {
        CsvToBean<AddressCsvBean> csvToBean = new CsvToBeanBuilder(reader)
                .withType(AddressCsvBean.class)
                .withSeparator(';')
                .build();
        return csvToBean.stream().collect(Collectors.toList());
    }

    private List<AddressCsvBean> readCsvGzAddressesUsingFastCsv(Path filePath, Reader reader) {
        val csvReader = CsvReader.builder()
                .fieldSeparator(';')
                .quoteCharacter('"')
                .commentStrategy(CommentStrategy.SKIP)
                .commentCharacter('#')
                .skipEmptyLines(true)
                .ofNamedCsvRecord(reader);
        return csvReader.stream()
                .map(AddressCsvBean::csvRecordToAddressBean)
                .collect(Collectors.toList());
    }


    private void readAndRegisterAddressesUsingOpenCsv(Path filePath) {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            CsvToBean<AddressCsvBean> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(AddressCsvBean.class)
                    .withSeparator(';')
                    .build();
            csvToBean.stream().forEach(this::registerAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readAndRegisterAddressesUsingChunkThreadsOpenCsv(Path filePath) {
        val execService = ForkJoinPool.commonPool();
        val csvParser = new CSVParserBuilder()
                .withSeparator(';').build();
        val csvReader = new FileChunkMultiThreadedOpenCsvReader<>(execService, csvParser, AddressCsvBean.class);
        csvReader.readAllLines(filePath, true, this::registerAddress);
    }

    private void readAndRegisterAddressesUsingFastCsv(Path filePath) {
        try (CsvReader<NamedCsvRecord> csvReader = CsvReader.builder()
                .fieldSeparator(';')
                .quoteCharacter('"')
                .commentStrategy(CommentStrategy.SKIP)
                .commentCharacter('#')
                .skipEmptyLines(true)
                .ofNamedCsvRecord(filePath)) {
            csvReader.forEach(this::registerAddressFastCsvRecord);
        } catch(IOException ex) {
            throw new RuntimeException("Failed to read", ex);
        }
    }

    private void registerAddressFastCsvRecord(NamedCsvRecord rec) {
        val addr = AddressCsvBean.namedCsvRecordToAddressBean(rec);
        registerAddress(addr);
    }

    private void registerAddressFastCsvRecord(CsvRecord rec) {
        val addr = AddressCsvBean.csvRecordToAddressBean(rec);
        registerAddress(addr);
    }

    private void readAndRegisterAddressesUsingChunkThreadsFastCsv(Path filePath) {
        val execService = ForkJoinPool.commonPool();
        val csvReaderBuilder = CsvReader.builder()
                .fieldSeparator(';')
                .quoteCharacter('"')
                .commentStrategy(CommentStrategy.SKIP)
                .commentCharacter('#')
                .skipEmptyLines(true);
        val csvReader = new FileChunkMultiThreadedFastCsvReader(execService, csvReaderBuilder);
        csvReader.readAllLines(filePath, true, this::registerAddressFastCsvRecord);
    }

    private void registerAddresses(Collection<AddressCsvBean> src) {
        for(val e : src) {
            registerAddress(e);
        }
    }

    private void registerAddress(AddressCsvBean src) {
        String zipCode = src.commune_insee;
        if (zipCode == null || zipCode.length() < 5) {
            // invalid zipCode, expecting 5
            return;
        }
        int dept = 10 * (zipCode.charAt(0)-'0') + (zipCode.charAt(1)-'0');
        if (! filterDeptsSet.contains(dept)) {
            currentFilteredAddresses++;
            return;
        }

        CoordDTO coord = new CoordDTO(src.longitude, src.lattitude);
        // register city + count
        String cityCode = src.commune_insee;
        CityDTO city = cityByZipCode.get(cityCode);
        if (city == null) {
            city = new CityDTO();
            city.id = cities.size()+1;
            city.name = src.commune_nom;
            city.zipCode = cityCode;
            city.coordSet.init(coord);

            cities.add(city);
            cityByZipCode.put(cityCode, city);
        }
        city.coordSet.addCoord(coord);
        city.addressCount++;

        // register street name + count
        String streetNameText = src.voie_nom;
        // extract common street type ("Rue", "Avenue", "Voie", "Place", ...)
        // and prefix "Rue de la", "Rue du", "Rue des", ...

        StreetNameDTO streetNameEntry = streetByName.get(streetNameText);
        if (streetNameEntry == null) {
            streetNameEntry = new StreetNameDTO();
            streetNameEntry.id = streetNames.size() + 1;
            streetNameEntry.name = streetNameText;

            streetNames.add(streetNameEntry);
            streetByName.put(streetNameText, streetNameEntry);
        }
        streetNameEntry.countAddress++;
        Integer prevCountByCity = streetNameEntry.countByCityZipCode.get(cityCode);
        int countByCity = 1 + ((prevCountByCity!=null)? prevCountByCity.intValue() : 0);
        streetNameEntry.countByCityZipCode.put(cityCode, countByCity);

        // register street in city
        CityStreetDTO cityStreet = city.streetById.get(streetNameEntry.id);
        if (cityStreet == null) {
            int cityStreetId = city.streetById.size()+1;
            cityStreet = new CityStreetDTO(cityStreetId, city.id, streetNameEntry.id);
            cityStreet.coordSet.init(coord);
            city.streetById.put(streetNameEntry.id, cityStreet);
            city.streets.add(cityStreet);
        }
        cityStreet.addressCount++;
        cityStreet.coordSet.addCoord(coord);

        // register address
        if (enableDetailedAddresses) {
            AddressDTO address = new AddressDTO(city.id, streetNameEntry.id,
                    src.numero, src.suffixe, coord);
            addresses.add(address);
        }

        currentLoadedAddresses++;
        progressNextDisplayLoadedAddresses--;
        if (progressNextDisplayLoadedAddresses <= 0) {
            log.info("... progress loading address: " + currentLoadedAddresses
                    + " (+ filtered:" + currentFilteredAddresses
                    + ", + skipped Files:" + currentFilteredFiles
                    + ")");
            progressNextDisplayLoadedAddresses = freqProgressLoadedAddresss;
        }
    }

}
