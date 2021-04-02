package fr.an.tests.parquetmetadata.dto;

import java.util.List;

import lombok.Data;

@Data
public class ParquetOffsetIndexDTO {

	/**
	 * PageLocations, ordered by increasing PageLocation.offset. It is required that
	 * page_locations[i].first_row_index < page_locations[i+1].first_row_index.
	 */
	List<ParquetPageLocationDTO> page_locations;

}
