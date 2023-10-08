package fr.an.tests.parquetmetadata.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.an.tests.parquetmetadata.dto.ScanDirFileMetadatasResultDTO;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetFileInfoDTO;
import fr.an.tests.parquetmetadata.service.ParquetMetadataService;

@RestController
@RequestMapping(path = "/api/parquet-metadata")
@Tag(name = "ParquetMetadataRest",
	description = "parquet metadata api")
public class ParquetMetadataRestController {

	@Autowired
	public ParquetMetadataService parquetMetadataService;
	
	@GetMapping(path="/readFileMetadata")
	@Operation(description = "read file metadata")
	public ParquetFileInfoDTO readFileMetadata(
			@RequestParam("file") String file) {
		return parquetMetadataService.readFileInfo(file);
	}

	@GetMapping(path="/scanDirFileMetadata")
	@Operation(description = "scan dir parquet files metadatas")
	public ScanDirFileMetadatasResultDTO scanDirFileMetadata(
			@RequestParam("baseDir") String baseDir) {
		return parquetMetadataService.scanDirFileMetadata(baseDir);
	}

}
