package fr.an.tests.parquetmetadata.dto;

import lombok.Data;

/**
 * statistics of a given page type and encoding
 */
@Data
public class ParquetPageEncodingStatsDTO {

	/** the page type (data/dic/...) **/
	ParquetPageType page_type;

	/** encoding of the page **/
	ParquetEncoding encoding;

	/** number of pages of this type with this encoding **/
	int count;

}
