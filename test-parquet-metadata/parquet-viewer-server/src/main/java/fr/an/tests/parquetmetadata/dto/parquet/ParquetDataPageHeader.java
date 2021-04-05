package fr.an.tests.parquetmetadata.dto.parquet;

import lombok.Data;

/** Data page header */
@Data
public class ParquetDataPageHeader {

	/** Number of values, including NULLs, in this data page. **/
	int numValues;

	/** Encoding used for this data page **/
	ParquetEncoding encoding;

	/** Encoding used for definition levels **/
	ParquetEncoding definitionLevelEncoding;

	/** Encoding used for repetition levels **/
	ParquetEncoding repetitionLevelEncoding;

	/** Optional statistics for the data in this page **/
	ParquetStatisticsDTO<?> statistics;

}
