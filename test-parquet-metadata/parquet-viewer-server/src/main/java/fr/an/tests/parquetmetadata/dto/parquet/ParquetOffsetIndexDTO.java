package fr.an.tests.parquetmetadata.dto.parquet;

import java.util.List;

import lombok.Data;

@Data
public class ParquetOffsetIndexDTO {

	/**
	 * PageLocations, ordered by increasing PageLocation.offset. It is required that
	 * pageLocations[i].firstRowIndex < pageLocations[i+1].firstRowIndex.
	 */
	List<ParquetPageLocationDTO> pageLocations;

}
