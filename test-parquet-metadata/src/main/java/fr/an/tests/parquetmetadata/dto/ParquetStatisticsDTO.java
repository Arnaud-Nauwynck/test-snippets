package fr.an.tests.parquetmetadata.dto;

import lombok.Data;

/**
 * Statistics per row group and per page All fields are optional.
 */
@Data
public class ParquetStatisticsDTO {

	/**
	 * DEPRECATED: min and max value of the column. Use min_value and max_value.
	 *
	 * Values are encoded using PLAIN encoding, except that variable-length byte
	 * arrays do not include a length prefix.
	 *
	 * These fields encode min and max values determined by signed comparison only.
	 * New files should use the correct order for a column's logical type and store
	 * the values in the min_value and max_value fields.
	 *
	 * To support older readers, these may be set when the column order is signed.
	 */
	byte[] max;
	byte[] min;

	/** count of null value in the column */
	Long null_count;
	/** count of distinct values occurring */
	Long distinct_count;
	/**
	 * Min and max values for the column, determined by its ColumnOrder.
	 *
	 * Values are encoded using PLAIN encoding, except that variable-length byte
	 * arrays do not include a length prefix.
	 */
	byte[] max_value;
	byte[] min_value;

}