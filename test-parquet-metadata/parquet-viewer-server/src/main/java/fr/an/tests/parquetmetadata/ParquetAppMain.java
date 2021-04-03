package fr.an.tests.parquetmetadata;

import java.io.File;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import fr.an.tests.parquetmetadata.service.ParquetMetadataService;


@SpringBootApplication
public class ParquetAppMain {

	public static void main(String[] args) {
		SpringApplication.run(ParquetAppMain.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ParquetMetadataService service) {
		return args -> {
			File f = new File("src/test/data/datapage_v2.snappy.parquet");
			String absPath = f.getAbsolutePath();
			String fileUrl = "file:///" + absPath;
			service.readFileMetadata(fileUrl);
		};
	}
}


