package fr.an.tests.parquetmetadata.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * cspecific metadata per Chunk ... cf implicit parent ParquetColumnMetaDataDTO
 */
@Data
public class ParquetColumnChunkMetaDataDTO {

	/** Number of values in this column **/
	long num_values;

	/**
	 * total byte size of all uncompressed pages in this column chunk (including the
	 * headers)
	 **/
	long total_uncompressed_size;

	/**
	 * total byte size of all compressed, and potentially encrypted, pages in this
	 * column chunk (including the headers)
	 **/
	long total_compressed_size;

	/** Optional key/value metadata **/
	@JsonInclude(Include.NON_NULL)
	Map<String, String> key_value_metadata;

	/** Byte offset from beginning of file to first data page **/
	Long data_page_offset;

	/** Byte offset from beginning of file to root index page **/
	Long index_page_offset;

	/** Byte offset from the beginning of file to first (only) dictionary page **/
	@JsonInclude(Include.NON_NULL)
	Long dictionary_page_offset;

	/** optional statistics for this column chunk */
	@JsonInclude(Include.NON_NULL)
	ParquetStatisticsDTO<?> statistics;

	/**
	 * Set of all encodings used for pages in this column chunk. This information
	 * can be used to determine if all data pages are dictionary encoded for example
	 **/
	List<ParquetPageEncodingStatsDTO> encoding_stats;

	/** Byte offset from beginning of file to Bloom filter data. **/
	@JsonInclude(Include.NON_NULL)
	Long bloom_filter_offset;

}
