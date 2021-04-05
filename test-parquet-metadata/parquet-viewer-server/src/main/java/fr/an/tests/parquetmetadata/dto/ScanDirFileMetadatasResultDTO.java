package fr.an.tests.parquetmetadata.dto;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import fr.an.tests.parquetmetadata.dto.parquet.ParquetSchemaElementDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ScanDirFileMetadatasResultDTO {

	String baseDir;
	
	@Data
	public static class PartitionScanStatisticsDTO {
		public String partitionColName;
		public Set<String> foundValues = new LinkedHashSet<>();
		public int count;
	}
	
	List<PartitionScanStatisticsDTO> partitionScanStatistics;
	
	List<ParquetSchemaElementDTO> schema;

	@Data @NoArgsConstructor @AllArgsConstructor
	public static class PartitionAndFileDataInfoDTO {
		public List<String> partitionValues;
		public String fileName;
		public ParquetDataFileInfoDTO dataInfo;
	}

	List<PartitionAndFileDataInfoDTO> partFileInfos;

}
