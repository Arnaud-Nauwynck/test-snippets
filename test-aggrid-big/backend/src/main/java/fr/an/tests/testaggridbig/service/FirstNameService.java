package fr.an.tests.testaggridbig.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import de.siegmar.fastcsv.reader.CommentStrategy;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRecord;
import fr.an.tests.testaggridbig.dto.*;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class FirstNameService {

    @Getter
    private boolean dataLoaded;

    @Value("${app.opendata.firstname.path}")
    private String openDataFirstnamePath;

    @Getter
    private List<FirstnameDTO> firstNames = new ArrayList<>();

    //---------------------------------------------------------------------------------------------

    /**
     * <PRE>
     * </PRE>
     */
    @PostConstruct
    public void init() {
        log.info("init -> async load all french firstnames");

        new Thread(() -> {
            log.info("load all french firstNames");
            long start = System.currentTimeMillis();
            loadData();
            this.dataLoaded = true;
            int millis = (int) (System.currentTimeMillis() - start);
            log.info("done loaded all french firstNames, took " + millis + " ms,"
                    + ", count:" + firstNames.size());
        }).start();
    }

    private void loadData() {
        Path filePath = Paths.get(openDataFirstnamePath);
        loadFirstNamesUsingOpenCsv(filePath);
    }

    private void loadFirstNamesUsingOpenCsv(Path filePath) {
        try (Reader reader = Files.newBufferedReader(filePath,
                Charset.forName("ISO-8859-1"))) {
            CsvToBean<FirstnameCsvBean> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(FirstnameCsvBean.class)
                    .withSeparator(';')
                    .build();
            csvToBean.stream().forEach(this::registerFirstName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void registerFirstName(FirstnameCsvBean src) {
        FirstnameDTO res = new FirstnameDTO();
        res.prenom = src.prenom;
        int indexPar = src.prenom.indexOf("(");
        if (indexPar != -1) {
            res.prenom = src.prenom.substring(0, indexPar).trim();
        }
        res.genre = src.genre;
        res.langage = src.langage;
        res.frequence = src.frequence;
        firstNames.add(res);
    }

}
