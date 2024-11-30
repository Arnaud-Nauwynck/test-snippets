package fr.an.tests.testaggridbig.util;

import com.opencsv.ICSVParser;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.bean.util.OpencsvUtils;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvReader.CsvReaderBuilder;
import de.siegmar.fastcsv.reader.CsvRecord;
import de.siegmar.fastcsv.reader.NamedCsvRecord;
import lombok.val;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class FileChunkMultiThreadedFastCsvReader {

    private final ExecutorService executorService;
    private final CsvReaderBuilder csvReaderBuilder;
    private final int chunkSize;

    public static final int DEFAULT_CHUNK_SIZE = 1024*1024;

    public FileChunkMultiThreadedFastCsvReader(ExecutorService executorService, CsvReaderBuilder csvReaderBuilder) {
        this(executorService, csvReaderBuilder, DEFAULT_CHUNK_SIZE);
    }

    public FileChunkMultiThreadedFastCsvReader(ExecutorService executorService, CsvReaderBuilder csvReaderBuilder, int chunkSize) {
        this.executorService = executorService;
        this.csvReaderBuilder = csvReaderBuilder;
        this.chunkSize = chunkSize;
    }


    public void readAllLines(
            Path filePath,
            boolean hasHeaderLine,
            Consumer<CsvRecord> callback
    ) {
        // Split the file into chunks
        long fileSize = 0;
        try {
            fileSize = Files.size(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int chunkCount = (int) (fileSize / chunkSize);

        List<Future<List<CsvRecord>>> chunkBeansFutures = new ArrayList<>();

        for (int i = 0; i < chunkCount; i++) {
            long start = i * chunkSize;
            long end = (i == chunkCount - 1) ? fileSize : start + chunkSize;

            chunkBeansFutures.add(executorService.submit(() -> {
                List<CsvRecord> res = new ArrayList<>(1000);
                try (RandomAccessFile raf = new RandomAccessFile(filePath.toString(), "r");
                     BufferedReader br = new BufferedReader(new InputStreamReader(
                             new FileInputStream(raf.getFD())))
                ) {
                    raf.seek(start);
                    if (start > 0) {
                        // Skip to the next newline character
                        raf.readLine();
                    } else {
                        // skip header line
                        if (hasHeaderLine) {
                            raf.readLine();
                        }
                    }

                    CsvReader<CsvRecord> csvReader = csvReaderBuilder.ofCsvRecord(br);
                    for(val csvRecord: csvReader) {
                        res.add(csvRecord);

                        if (raf.getFilePointer() >= end) { // TODO BUG buffer count
                            break;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return res;
            }));
        }

        // from main thread, get chunk results, and send to callback
        for (Future<List<CsvRecord>> chunkBeansFuture : chunkBeansFutures) {
            List<CsvRecord> beans = null;
            try {
                beans = chunkBeansFuture.get();
            } catch (InterruptedException|ExecutionException e) {
                throw new RuntimeException(e);
            }
            for(val bean : beans) {
                callback.accept(bean);
            }
        }
    }
}
