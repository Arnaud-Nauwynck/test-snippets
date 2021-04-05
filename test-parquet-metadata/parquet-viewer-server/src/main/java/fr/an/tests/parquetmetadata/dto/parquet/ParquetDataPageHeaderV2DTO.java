package fr.an.tests.parquetmetadata.dto.parquet;

/**
 * New page format allowing reading levels without decompressing the data
 * Repetition and definition levels are uncompressed The remaining section
 * containing the data is compressed if isCompressed is true
 **/
public class ParquetDataPageHeaderV2DTO {

	/** Number of values, including NULLs, in this data page. **/
	int numValues;
	  
	/**
	 * Number of NULL values, in this data page. Number of non-null = numValues -
	 * numNulls which is also the number of values in the data section
	 **/
	int numNulls;
	
	/**
	 * Number of rows in this data page. which means pages change on record
	 * boundaries (r = 0)
	 **/
	int numRows;
	
	/** Encoding used for data in this page **/
	ParquetEncoding encoding;

	// repetition levels and definition levels are always using RLE (without size in it)

	/** length of the definition levels */
	int definitionLevelsByteLength;
	/** length of the repetition levels */
	int repetitionLevelsByteLength;

	/**
	 * whether the values are compressed. Which means the section of the page
	 * between definitionLevelsByteLength + repetitionLevelsByteLength + 1 and
	 * compressedPageSize (included) is compressed with the compressionCodec. If
	 * missing it is considered compressed
	 */
	boolean isCompressed = true;

	/** optional statistics for this column chunk */
	ParquetStatisticsDTO<?> statistics;

}
