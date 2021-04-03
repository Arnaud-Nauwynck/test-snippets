package fr.an.tests.parquetmetadata.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.an.tests.parquetmetadata.dto.ParquetFileMetadataDTO;
import fr.an.tests.parquetmetadata.service.ParquetMetadataService;

@RestController
@RequestMapping(path = "/api/parquet-metadata")
public class ParquetMetadataRestController {

	@Autowired
	public ParquetMetadataService parquetMetadataService;
	
	@GetMapping(path="/readFileMetadata")
	public ParquetFileMetadataDTO readFileMetadata(
			@RequestParam("file") String file) {
		return parquetMetadataService.readFileMetadata(file);
	}

}
