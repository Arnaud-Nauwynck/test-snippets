package fr.an.tests.parquetmetadata.dto.parquet;

import lombok.Data;

/**
 * Union to specify the order used for the minValue and maxValue fields for a
 * column. This union takes the role of an enhanced enum that allows rich
 * elements (which will be needed for a collation-based ordering in the future).
 *
 * Possible values are:
 * * TypeDefinedOrder - the column uses the order defined by its logical or
 *                      physical type (if there is no logical type).
 *
 * If the reader does not support the value of this union, min and max stats
 * for this column should be ignored.
 */
@Data
public class ParquetColumnOrderDTO {
	//union

	/**
	 * The sort orders for logical types are:
	 *   UTF8 - unsigned byte-wise comparison
	 *   INT8 - signed comparison
	 *   INT16 - signed comparison
	 *   INT32 - signed comparison
	 *   INT64 - signed comparison
	 *   UINT8 - unsigned comparison
	 *   UINT16 - unsigned comparison
	 *   UINT32 - unsigned comparison
	 *   UINT64 - unsigned comparison
	 *   DECIMAL - signed comparison of the represented value
	 *   DATE - signed comparison
	 *   TIMEMILLIS - signed comparison
	 *   TIMEMICROS - signed comparison
	 *   TIMESTAMPMILLIS - signed comparison
	 *   TIMESTAMPMICROS - signed comparison
	 *   INTERVAL - unsigned comparison
	 *   JSON - unsigned byte-wise comparison
	 *   BSON - unsigned byte-wise comparison
	 *   ENUM - unsigned byte-wise comparison
	 *   LIST - undefined
	 *   MAP - undefined
	 *
	 * In the absence of logical types, the sort order is determined by the physical type:
	 *   BOOLEAN - false, true
	 *   INT32 - signed comparison
	 *   INT64 - signed comparison
	 *   INT96 (only used for legacy timestamps) - undefined
	 *   FLOAT - signed comparison of the represented value (*)
	 *   DOUBLE - signed comparison of the represented value (*)
	 *   BYTEARRAY - unsigned byte-wise comparison
	 *   FIXEDLENBYTEARRAY - unsigned byte-wise comparison
	 *
	 * (*) Because the sorting order is not specified properly for floating
	 *     point values (relations vs. total ordering) the following
	 *     compatibility rules should be applied when reading statistics:
	 *     - If the min is a NaN, it should be ignored.
	 *     - If the max is a NaN, it should be ignored.
	 *     - If the min is +0, the row group may contain -0 values as well.
	 *     - If the max is -0, the row group may contain +0 values as well.
	 *     - When looking for NaN values, min and max should be ignored.
	 */
	TypeDefinedOrder TYPEORDER;

	/** Empty struct to signal the order defined by the physical or logical type */
	public static class TypeDefinedOrder {}


}
