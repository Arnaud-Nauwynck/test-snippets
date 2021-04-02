package fr.an.tests.parquetmetadata.service;


import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.BlockMetaData;
import org.apache.parquet.hadoop.metadata.FileMetaData;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.InputFile;
import org.springframework.stereotype.Service;

import fr.an.tests.parquetmetadata.dto.ParquetFileMetadataDTO;

@Service
public class ParquetMetadataService {

	public ParquetFileMetadataDTO readFileMetadata(String file) {
		ParquetFileReader fileReader = createParquetFileReader(file);

		return parquetFileReaderToDTO(fileReader);
	}


	private ParquetFileReader createParquetFileReader(String file) {
		Configuration conf = new Configuration(); 
		// File uri = new File(file).toURI().toURL().toString();
		Path path = new Path(file);
		InputFile intputFile;
		try {
			intputFile = HadoopInputFile.fromPath(path, conf);
		} catch (IOException ex) {
			throw new RuntimeException("", ex);
		}
		ParquetReadOptions readOptions = ParquetReadOptions.builder().build();
		
		ParquetFileReader fileReader;
		try {
			fileReader = ParquetFileReader.open(intputFile, readOptions);
		} catch (IOException ex) {
			throw new RuntimeException("", ex);
		}
		return fileReader;
	}


	private ParquetFileMetadataDTO parquetFileReaderToDTO(ParquetFileReader fileReader) {
		ParquetFileMetadataDTO res = new ParquetFileMetadataDTO();
		
		FileMetaData fileMetaData = fileReader.getFileMetaData();
		ParquetMetadata fileFooter = fileReader.getFooter();
		List<BlockMetaData> rowGroups = fileReader.getRowGroups();

		// TODO
		
		return res;
	}
}
