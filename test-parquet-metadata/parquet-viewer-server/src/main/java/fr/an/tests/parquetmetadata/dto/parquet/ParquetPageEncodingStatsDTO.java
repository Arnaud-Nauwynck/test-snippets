package fr.an.tests.parquetmetadata.dto.parquet;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * statistics of a given page type and encoding
 */
@Data @AllArgsConstructor
public class ParquetPageEncodingStatsDTO {

	/** the page type (data/dic/...) **/
	ParquetPageType pageType;

	/** encoding of the page **/
	ParquetEncoding encoding;

	/** number of pages of this type with this encoding **/
	int count;

}
