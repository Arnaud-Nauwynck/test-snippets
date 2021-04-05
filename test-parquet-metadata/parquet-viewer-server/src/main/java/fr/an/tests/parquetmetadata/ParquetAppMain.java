package fr.an.tests.parquetmetadata;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.an.tests.parquetmetadata.dto.parquet.ParquetFileInfoDTO;
import fr.an.tests.parquetmetadata.service.ParquetMetadataService;
import lombok.val;


@SpringBootApplication
public class ParquetAppMain {

	public static void main(String[] args) {
		SpringApplication.run(ParquetAppMain.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ParquetMetadataService service) {
		return args -> {
			String file = "src/test/data/datapage_V2.snappy.parquet";
			ParquetFileInfoDTO fileInfo = service.readFileInfo(file);
			val om = new ObjectMapper();
			om.configure(SerializationFeature.INDENT_OUTPUT, true);
			// om.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			String json = om.writeValueAsString(fileInfo);
			System.out.println("sample parquet file info: \n" + json);
		};
	}
}


