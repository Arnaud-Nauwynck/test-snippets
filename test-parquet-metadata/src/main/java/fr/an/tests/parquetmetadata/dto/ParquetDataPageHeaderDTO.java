package fr.an.tests.parquetmetadata.dto;

import lombok.Data;

/** Data page header */
@Data
public class ParquetDataPageHeaderDTO {

	/** Number of values, including NULLs, in this data page. **/
	int num_values;

	/** Encoding used for this data page **/
	ParquetEncoding encoding;

	/** Encoding used for definition levels **/
	ParquetEncoding definition_level_encoding;

	/** Encoding used for repetition levels **/
	ParquetEncoding repetition_level_encoding;

	/** Optional statistics for the data in this page**/
	ParquetStatisticsDTO statistics;

}
