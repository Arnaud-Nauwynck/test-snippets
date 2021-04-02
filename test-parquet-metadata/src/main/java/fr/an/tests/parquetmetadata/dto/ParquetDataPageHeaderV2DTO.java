package fr.an.tests.parquetmetadata.dto;

/**
 * New page format allowing reading levels without decompressing the data
 * Repetition and definition levels are uncompressed The remaining section
 * containing the data is compressed if is_compressed is true
 **/
public class ParquetDataPageHeaderV2DTO {

	/** Number of values, including NULLs, in this data page. **/
	int num_values;
	  
	/**
	 * Number of NULL values, in this data page. Number of non-null = num_values -
	 * num_nulls which is also the number of values in the data section
	 **/
	int num_nulls;
	
	/**
	 * Number of rows in this data page. which means pages change on record
	 * boundaries (r = 0)
	 **/
	int num_rows;
	
	/** Encoding used for data in this page **/
	ParquetEncoding encoding;

	// repetition levels and definition levels are always using RLE (without size in it)

	/** length of the definition levels */
	int definition_levels_byte_length;
	/** length of the repetition levels */
	int repetition_levels_byte_length;

	/**
	 * whether the values are compressed. Which means the section of the page
	 * between definition_levels_byte_length + repetition_levels_byte_length + 1 and
	 * compressed_page_size (included) is compressed with the compression_codec. If
	 * missing it is considered compressed
	 */
	Boolean is_compressed = true;

	/** optional statistics for this column chunk */
	ParquetStatisticsDTO statistics;

}
