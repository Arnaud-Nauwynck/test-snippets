package fr.an.tests.parquetmetadata.dto.parquet;

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
	long numValues;

	/**
	 * total byte size of all uncompressed pages in this column chunk (including the
	 * headers)
	 **/
	long totalUncompressedSize;

	/**
	 * total byte size of all compressed, and potentially encrypted, pages in this
	 * column chunk (including the headers)
	 **/
	long totalCompressedSize;

	/** Optional key/value metadata **/
	@JsonInclude(Include.NON_NULL)
	Map<String, String> keyValueMetadata;

	/** Byte offset from beginning of file to first data page **/
	Long dataPageOffset;

	/** Byte offset from beginning of file to root index page **/
	Long indexPageOffset;

	/** Byte offset from the beginning of file to first (only) dictionary page **/
	@JsonInclude(Include.NON_NULL)
	Long dicPageOffset;

	/** optional statistics for this column chunk */
	@JsonInclude(Include.NON_NULL)
	ParquetStatisticsDTO<?> statistics;

	/**
	 * Set of all encodings used for pages in this column chunk. This information
	 * can be used to determine if all data pages are dictionary encoded for example
	 **/
	List<ParquetPageEncodingStatsDTO> encodingStats;

	/** Byte offset from beginning of file to Bloom filter data. **/
	@JsonInclude(Include.NON_NULL)
	Long bloomFilterOffset;

}
