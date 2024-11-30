package fr.an.tests.testaggridbig.util;

import com.opencsv.ICSVParser;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.bean.util.OpencsvUtils;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
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

public class FileChunkMultiThreadedOpenCsvReader<T> {

    private final ExecutorService executorService;
    private final ICSVParser csvParser;
    private final MappingStrategy<T> mappingStrategy;
    private final int chunkSize;

    public static final int DEFAULT_CHUNK_SIZE = 1024*1024;

    public FileChunkMultiThreadedOpenCsvReader(ExecutorService executorService, ICSVParser csvParser, Class<T> beanType) {
        this(executorService, csvParser, beanType, DEFAULT_CHUNK_SIZE);
    }

    public FileChunkMultiThreadedOpenCsvReader(ExecutorService executorService, ICSVParser csvParser, Class<T> beanType, int chunkSize) {
        this.executorService = executorService;
        this.csvParser = csvParser;
        this.mappingStrategy = OpencsvUtils.determineMappingStrategy(beanType, null, null);
        T bean;
        try {
            bean = beanType.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            // TODO does not work!.. random order!
            mappingStrategy.generateHeader(bean);
        } catch (CsvRequiredFieldEmptyException e) {
            throw new RuntimeException(e);
        }

        this.chunkSize = chunkSize;
    }


    public void readAllLines(
            Path filePath,
            boolean hasHeaderLine,
            Consumer<T> callback
    ) {
        // Split the file into chunks
        long fileSize = 0;
        try {
            fileSize = Files.size(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int chunkCount = (int) (fileSize / chunkSize);

        List<Future<List<T>>> chunkBeansFutures = new ArrayList<>();

        for (int i = 0; i < chunkCount; i++) {
            long start = i * chunkSize;
            long end = (i == chunkCount - 1) ? fileSize : start + chunkSize;

            chunkBeansFutures.add(executorService.submit(() -> {
                List<T> res = new ArrayList<>(1000);
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

                    for(;;) {
                        String line = br.readLine();
                        if (line == null) {
                            break;
                        }
                        String[] csvLine = csvParser.parseLineMulti(line);
                        T bean = mappingStrategy.populateNewBean(csvLine);
                        res.add(bean);

                        if (raf.getFilePointer() >= end) { // TODO BUG buffer count
                            break;
                        }
                    }
                } catch (IOException | CsvException e) {
                    throw new RuntimeException(e);
                }
                return res;
            }));
        }

        // from main thread, get chunk results, and send to callback
        for (Future<List<T>> chunkBeansFuture : chunkBeansFutures) {
            List<T> beans = null;
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
