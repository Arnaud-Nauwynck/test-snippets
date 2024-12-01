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

    @Value("${app.opendata.bal.filterDeptRanges}")
    private String filterDeptRanges;
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
    private List<PrefixStreetTypeDTO> prefixStreetTypes = new ArrayList<>();

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
        registerPrefixStreetTypes();
        log.info("init -> async load all addresses");

        this.filterDeptsSet = new HashSet();
        if (filterDeptRanges.isEmpty()) {
            for (int dept = 1; dept <= 99; dept++) {
                filterDeptsSet.add(dept);
            }
            for (int dept = 901; dept <= 999; dept++) {
                filterDeptsSet.add(dept);
            }
        } else {
            for (String deptRange : filterDeptRanges.split(",")) {
                if (deptRange.contains("-")) {
                    val fromTo = deptRange.split("-");
                    int from = Integer.parseInt(fromTo[0]);
                    int to = Integer.parseInt(fromTo[1]);
                    for (int dept = from; dept <= to; dept++) {
                        filterDeptsSet.add(dept);
                    }
                } else {
                    filterDeptsSet.add(Integer.parseInt(deptRange));
                }
            }
        }
        log.info("Filter addresses with dept ranges in '" + filterDeptRanges + "' => expanded depts set: " + filterDeptsSet);

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
                    + ", filteredAddresses:" + currentFilteredAddresses
                    + ", skipped files:" + currentFilteredFiles);
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

    private static boolean isNumber(String text) {
        if (text == null || text.isEmpty()) return false;
        int len = text.length();
        for(int i = 0; i < len; i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
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
        val streetNameTextFull = src.voie_nom;
        String streetNameText = streetNameTextFull;
        // extract common street type ("Rue", "Avenue", "Voie", "Place", ...)
        // and prefix "Rue de la", "Rue du", "Rue des", ...
        String streetNameLowerText = streetNameText.toLowerCase();
        // ex "7S1 Place du"... => need first prefix before "Place du" ??!  or fix invalid BAL database
        String streetPrefix = null;
        if (streetNameLowerText.length() > 0 && Character.isDigit(streetNameLowerText.charAt(0))) {
            int sep = streetNameLowerText.indexOf(' ');
            streetPrefix = streetNameLowerText.substring(0, sep);
            streetNameLowerText = streetNameLowerText.substring(sep+1);
            streetNameText = streetNameText.substring(sep+1);

            if (isNumber(streetPrefix)) {
                int num = Integer.parseInt(streetPrefix);
                if (src.numero == 0) {
                    src.numero = num;
                    streetPrefix = null;
                } else if (num == src.numero) {
                    // duplicate!
                    streetPrefix = null;
                }
            }
        }
        PrefixStreetTypeDTO originPrefixStreetType = findPrefixStreetFor(streetNameLowerText);
        PrefixStreetTypeDTO prefixStreetType = originPrefixStreetType;
        if (prefixStreetType != null && prefixStreetType.renormEntry != null) {
            // replace by renorm
            prefixStreetType = prefixStreetType.renormEntry;
        }
        if (prefixStreetType == null || prefixStreetType.streetTypeAndPrefix.isEmpty()) {
            if (streetNameLowerText.indexOf(' ') != -1
                    && !( streetNameLowerText.startsWith("le ") || streetNameLowerText.startsWith("la ")  || streetNameLowerText.startsWith("les ")
                    )
            ) {
                // debug?
                int dbg = 0;
                // prefixStreetType = findPrefixStreetFor(streetNameLowerText);
            }
        }
        String renormStreetTypeAndName;
        String streetName;
        if (originPrefixStreetType != null && ! originPrefixStreetType.renormStreetTypeAndPrefix.isEmpty()) {
            streetName = streetNameText.substring(originPrefixStreetType.streetTypeAndPrefix.length());
            renormStreetTypeAndName = prefixStreetType.renormStreetTypeAndPrefix + streetName;
        } else {
            streetName = streetNameText;
            renormStreetTypeAndName = streetNameText;
        }

        renormStreetTypeAndName = renormStreetTypeAndName.toLowerCase(); // ??

        StreetNameDTO streetNameEntry = streetByName.get(renormStreetTypeAndName); // streetNameText); ???
        if (streetNameEntry == null) {
            streetNameEntry = new StreetNameDTO();
            streetNameEntry.id = streetNames.size() + 1;
            streetNameEntry.pre = streetPrefix;
            streetNameEntry.typeId = (prefixStreetType!=null)? prefixStreetType.id : 0;
            streetNameEntry.name = streetName;

            streetNames.add(streetNameEntry);
            streetByName.put(renormStreetTypeAndName, streetNameEntry);
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

    protected void registerPrefixStreetTypes() {
        // invalid id: 0
        val type0 = new PrefixStreetTypeDTO();
        type0.id = 0;
        type0.streetType = "";
        type0.renormStreetType = "";
        type0.addressPrefix = null;
        type0.streetTypeAndPrefix = "??";
        type0.renormStreetTypeAndPrefix = "??";
        prefixStreetTypes.add(type0);

        registerStreetType("rue ");
        registerStreetType("rues ");
        registerStreetTypeRenorm("r ", "rue ");
        registerStreetType("route départementale ");
        registerStreetTypeRenorm("rd ", "route départementale ");
        registerStreetType("route nationale ");
        registerStreetTypeRenorm("rn ", "route nationale ");
        registerStreetType("route ");
        registerStreetTypeRenorm("rte ", "route ");
        registerStreetType("routes ");
        registerStreetType("avenue ");
        registerStreetType("avenues ");
        registerStreetTypeRenorm("av ", "avenue ");
        registerStreetType("boulevard ");
        registerStreetType("boulevards ");
        registerStreetTypeRenorm("bvd ", "boulevard ");
        registerStreetTypeRenorm("bd ", "boulevard ");
        registerStreetType("allée ");
        registerStreetTypeRenorm("all ", "allée ");
        registerStreetTypeRenorm("alleé ", "allée ");
        registerStreetTypeRenorm("allee ", "allée ");
        registerStreetType("allées ");
        registerStreetTypeRenorm("allees ", "allées ");
        registerStreetType("contre allée ");

        registerStreetType("place ");
        registerStreetType("places ");
        registerStreetTypeRenorm("pl ", "place");
        registerStreetTypeRenorm("pl. ", "place");
        registerStreetType("rond-point ");
        registerStreetTypeRenorm("rond point ", "rond-point ");
        registerStreetType("carrefour ");
        registerStreetTypeRenorm("carefour ", "carrefour ");
        registerStreetType("square ");
        registerStreetType("squares ");
        registerStreetTypeRenorm("sq. ", "square");
        registerStreetType("bel air ");

        registerStreetType("ruelle ");
        registerStreetType("ruelles ");
        registerStreetType("sentier ");
        registerStreetType("sentiers ");
        registerStreetType("sente "); // ?
        registerStreetType("voie ");
        registerStreetType("voies ");
        registerStreetType("voirie ");
        registerStreetType("impasse ");
        registerStreetType("petite impasse ");
        registerStreetType("enclave ");
        // registerStreetType("impasses ");
        registerStreetType("traverse ");
        registerStreetType("ruette ");
        registerStreetTypeRenorm("imp ", "impasse ");
        registerStreetType("bout ");
        registerStreetType("chemin ");
        registerStreetType("chemins ");
        registerStreetType("cheminement ");
        registerStreetType("passage ");
        registerStreetType("passages ");
        registerStreetType("chaussée ");
        registerStreetTypeRenorm("chaussee ", "chaussée ");
        registerStreetType("promenade ");
        registerStreetType("deviation chem ");
        registerStreetTypeRenorm("deviation ", "déviation ");
        registerStreetType("autoroute ");
        registerStreetType("aire de repos ");
        registerStreetType("rocade ");

        registerStreetType("parvis ");
        registerStreetType("souterrain ");
        registerStreetType("parking ");
        registerStreetType("esplanade ");
        registerStreetType("terrasse ");
        registerStreetType("espace ");
        registerStreetType("galerie ");
        registerStreetType("galeries ");

        registerStreetType("ancienne route ");
        registerStreetType("vieille route ");
        registerStreetType("petite route ");
        registerStreetType("grande avenue ");
        registerStreetType("ancienne rue ");
        registerStreetType("vielle rue ");
        registerStreetType("petite rue ");
        registerStreetType("grand-rue ");
        registerStreetTypeRenorm("grande rue ", "grand-rue ");
        registerStreetTypeRenorm("grand rue ", "grand-rue ");
        registerStreetTypeRenorm("grand'rue ", "grand-rue ");
        registerStreetType("basse rue ");
        registerStreetType("sur le chemin ");
        registerStreetType("grand chemin ");
        registerStreetType("petit chemin ");
        registerStreetType("ancien chemin ");
        registerStreetType("vieux chemin ");

        registerStreetType("hameau ");
        registerStreetTypeRenorm("ham ", "hameau ");
        registerStreetType("hameaux ");
        registerStreetType("cité ");
        registerStreetType("cités ");
        registerStreetTypeRenorm("cite ", "cité ");
        registerStreetTypeRenorm("citee ", "cité ");
        registerStreetType("camp ");
        registerStreetType("vers ");
        registerStreetType("quartier ");
        // registerStreetType("quartiers ");

        registerStreetType("quai ");
        registerStreetType("quais ");
        registerStreetType("passerelle ");
        registerStreetType("pont ");
        registerStreetType("ponts ");
        registerStreetType("île ");
        registerStreetTypeRenorm("ile ", "île ");
        registerStreetType("port ");
        registerStreetType("digue ");
        registerStreetType("bassin ");
        registerStreetType("fond ");
        registerStreetType("fonds ");
        registerStreetType("corniche ");
        registerStreetType("mare ");
        registerStreetType("fontaine ");
        registerStreetType("écluse ");
        registerStreetTypeRenorm("ecluse ", "écluse ");
        registerStreetType("lac ");
        registerStreetType("puit ");
        registerStreetType("puits ");
        registerStreetType("etang ");
        registerStreetType("fosse ");
        registerStreetType("haute fosse ");
        registerStreetType("traversin ");
        registerStreetType("traversière ");
        registerStreetType("barrage ");
        registerStreetType("pointe ");

        registerStreetType("triage ");
        registerStreetType("échangeur ");
        registerStreetTypeRenorm("echangeur ", "échangeur ");
        registerStreetType("rpa "); // ?
        registerStreetType("zac ");
        registerStreetType("za ");
        registerStreetType("zi ");
        registerStreetType("zone industrielle ");
        registerStreetType("zone artisanale ");
        registerStreetTypeRenorm("zone a.i. ", "zone d'activité industrielle");
        registerStreetTypeRenorm("zone a.e. ", "zone d'activité économique");
        registerStreetType("zone d'activité ");
        registerStreetTypeRenorm("zone d'activite ", "zone d'activité ");
        registerStreetTypeRenorm("zone d'activités ", "zone d'activité ");
        registerStreetType("zone ");
        registerStreetType("centre commercial ");
        registerStreetTypeRenorm("ctre commercial ", "centre commercial ");
        registerStreetTypeRenorm("centre comm ", "centre commercial ");
        registerStreetType("mail "); // "mail du centre commercial"?

        registerStreetType("groupe scolaire ");
        registerStreetType("école élémentaire");
        registerStreetType("école primaire");
        registerStreetType("école secondaire");
        registerStreetType("école ");
        registerStreetTypeRenorm("ecole ", "école ");
        registerStreetType("arrêt de bus ");
        registerStreetType("centre de loisirs ");
        registerStreetType("bibliothèque ");
        registerStreetType("mairie ");
        registerStreetType("église ");
        registerStreetType("eglise ");
        registerStreetType("cimetière ");
        registerStreetType("centre ");

        registerStreetType("péristyle ");
        registerStreetType("lieu dit ");
        registerStreetTypeRenorm("lieudit ", "lieu dit ");
        registerStreetType("résidence ");
        registerStreetType("résidences ");
        registerStreetTypeRenorm("residence ", "résidence ");
        registerStreetTypeRenorm("residences ", "résidences ");
        registerStreetType("immeuble ");
        registerStreetType("immeubles ");
        registerStreetTypeRenorm("imm ", "immeuble ");
        registerStreetType("batiment ");
        registerStreetTypeRenorm("bat ", "batiment ");
        registerStreetType("cave ");
        registerStreetType("caves ");
        registerStreetType("cavée ");
        registerStreetTypeRenorm("cavee ", "cavée ");
        registerStreetType("camping ");
        registerStreetType("château ");
        registerStreetType("chateau ");
        registerStreetType("haras ");
        registerStreetType("domaine ");
        registerStreetType("faubourg ");
        registerStreetType("pavillon ");
        registerStreetType("villa ");
        registerStreetType("villas ");
        registerStreetType("salle ");
        registerStreetType("porte ");
        registerStreetType("portes ");
        registerStreetType("tour ");
        registerStreetType("ferme ");
        registerStreetType("fermes ");
        registerStreetType("grange ");
        registerStreetType("cour ");
        registerStreetType("cours ");
        registerStreetType("clos ");
        registerStreetType("res du clos ");
        registerStreetType("cabanne ");
        registerStreetType("métairie ");
        registerStreetType("rampe ");
        registerStreetType("escalier ");
        registerStreetType("escaliers ");
        registerStreetType("grille ");
        registerStreetType("grilles ");
        registerStreetType("arcade ");
        registerStreetType("arcades ");
        registerStreetType("cote ");
        registerStreetType("côte ");
        registerStreetType("plaine ");
        registerStreetType("petite plaine ");
        registerStreetType("parc ");
        registerStreetType("prés ");
        registerStreetType("res "); // ?
        registerStreetType("pres "); // typo?
        registerStreetType("pre "); // typo?
        registerStreetType("pr ");
        registerStreetType("mesnil ");
        registerStreetType("jardin ");
        registerStreetType("grand jardin ");
        registerStreetType("petit jardin ");
        registerStreetType("champs ");
        registerStreetType("champ ");
        registerStreetType("terre ");
        registerStreetType("terres ");
        registerStreetType("prairie ");
        registerStreetType("prairies ");
        registerStreetType("grande prairie ");
        registerStreetType("petite prairie ");
        registerStreetType("clairière ");
        registerStreetType("enclos ");
        registerStreetType("lotissement ");
        registerStreetType("village ");
        registerStreetType("canton ");
        registerStreetType("croix ");
        registerStreetType("val ");
        registerStreetType("vallon ");
        registerStreetType("vallée ");
        registerStreetType("vallées ");
        registerStreetType("haut ");
        registerStreetType("bas ");
        registerStreetType("descente ");
        registerStreetType("montée ");
        registerStreetType("mont ");
        registerStreetType("monts ");
        registerStreetType("butte ");
        registerStreetType("coteaux ");
        registerStreetType("coteau "); // typo?
        registerStreetType("plateau ");
        registerStreetType("col ");
        registerStreetType("forêt ");
        registerStreetType("forêts ");
        registerStreetTypeRenorm("foret ", "forêt ");
        registerStreetType("bois ");
        registerStreetType("bosc ");
        registerStreetType("boscs ");
        registerStreetType("maison forestière ");
        registerStreetTypeRenorm("maison forestiere ", "maison forestière ");
        registerStreetTypeRenorm("mf ", "maison forestière ");
        registerStreetType("venelle ");
        registerStreetType("relais ");
        registerStreetType("moulin ");
        // registerStreetType("moulins ");
        registerStreetType("manoir ");
        registerStreetType("hétraie ");

        // street type with no trailing space?
        if (false) {
            // no trailing space!
            registerStreetType("grande avenue");
            registerStreetType("petite rue");
            registerStreetType("grande rue");
            registerStreetType("grand rue");
            registerStreetType("basse rue");
            registerStreetType("haute fosse");
            registerStreetType("zone industrielle");
            registerStreetType("zone artisanale");
            registerStreetType("grand jardin");
            registerStreetType("petit jardin");
            registerStreetType("grande prairie");
            registerStreetType("petite prairie");
        }

        // registerStreetType(""); // ?

        // resolve all corresponding renorm entries
        for(val prefixStreetType: prefixStreetTypes) {
            if (! prefixStreetType.renormStreetTypeAndPrefix.equals(prefixStreetType.streetTypeAndPrefix)) {
                PrefixStreetTypeDTO renormEntry = findPrefixStreetFor(prefixStreetType.renormStreetTypeAndPrefix);
                if (renormEntry == null) {
                    log.warn("should not occur");
                    renormEntry = prefixStreetType;
                }
                prefixStreetType.renormEntry = renormEntry;
            }
        }


        checkFindPrefixStreetFor("rte de la chapelle");
        checkFindPrefixStreetFor("butte de ");
    }

    protected void checkFindPrefixStreetFor(String text) {
        String lowerText = text.toLowerCase();
        val prefixStreet = findPrefixStreetFor(lowerText);
        if (null == prefixStreet || prefixStreet.streetTypeAndPrefix.isEmpty()) {
            log.info("should not occur: streetType not found for '" + lowerText + "'");
        }
    }

    protected void registerStreetType(String streetType) {
        registerStreetTypeRenorm(streetType, streetType);
    }
    protected void registerStreetTypeRenorm(String streetType, String renormStreetType) {
        registerStreetType_allDeterminants(streetType, renormStreetType);
        // TOOCHECK TODO
//        if (streetType.contains("'")) {
//            registerStreetType_allDeterminants(streetType.replace("'", "’"), renormStreetType.replace("'", "’"));
//        }
    }

    protected void registerStreetType_allDeterminants(String streetType, String renormStreetType) {
        registerPrefixStreetType(streetType, renormStreetType, "de la ");
        registerPrefixStreetType(streetType, renormStreetType, "de l'");
        registerPrefixStreetType(streetType, renormStreetType, "de l’"); // change ' by ’
        registerPrefixStreetType(streetType, renormStreetType, "de ");
        registerPrefixStreetType(streetType, renormStreetType, "du ");
        registerPrefixStreetType(streetType, renormStreetType, "des ");
        registerPrefixStreetType(streetType, renormStreetType, "le ");
        registerPrefixStreetType(streetType, renormStreetType, "les ");
        registerPrefixStreetType(streetType, renormStreetType, "l'");
        registerPrefixStreetType(streetType, renormStreetType, "l’"); // change ' by ’
        registerPrefixStreetType(streetType, renormStreetType, "la ");
        registerPrefixStreetType(streetType, renormStreetType, "dans ");
        registerPrefixStreetType(streetType, renormStreetType, "à la ");
        registerPrefixStreetType(streetType, renormStreetType, "à ");
        registerPrefixStreetType(streetType, renormStreetType, "aux ");
        registerPrefixStreetType(streetType, renormStreetType, "");
    }

    protected PrefixStreetTypeDTO registerPrefixStreetType(String streetType, String renormStreetType,
                                                           String addressPrefix) {
        val res = new PrefixStreetTypeDTO();
        res.id = prefixStreetTypes.size(); // no need +1, list contains id 0
        res.streetType = streetType;
        res.renormStreetType = renormStreetType;
        res.addressPrefix = addressPrefix;
        res.streetTypeAndPrefix = streetType + addressPrefix;
        res.renormStreetTypeAndPrefix = renormStreetType + addressPrefix;

        prefixStreetTypes.add(res);
        return res;
    }

    protected PrefixStreetTypeDTO findPrefixStreetFor(String text) {
        for(val prefixStreetType : prefixStreetTypes) {
            if (text.startsWith(prefixStreetType.streetTypeAndPrefix)) {
                if (prefixStreetType.id == 0) continue; // ignore id 0
                return prefixStreetType;
            }
        }
        return null;
    }

}
