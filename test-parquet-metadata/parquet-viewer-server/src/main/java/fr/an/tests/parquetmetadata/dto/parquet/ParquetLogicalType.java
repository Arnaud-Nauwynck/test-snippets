package fr.an.tests.parquetmetadata.dto.parquet;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * LogicalType annotations to replace ConvertedType.
 *
 * To maintain compatibility, implementations using LogicalType for a
 * SchemaElement must also set the corresponding ConvertedType from the
 * following table.
 */
public abstract class ParquetLogicalType {

	// Union ....
	// cf also ParquetConvertedType  (+ "NULL" for always null column, "UUID", ... )
	public enum ParquetLogicalTypeEnum {
		STRING, // use ConvertedType UTF8
		MAP, // use ConvertedType MAP
		LIST, // use ConvertedType LIST
		ENUM, // use ConvertedType ENUM
		DECIMAL, // use ConvertedType DECIMAL
		DATE, // use ConvertedType DATE
	
		// use ConvertedType TIME_MICROS for TIME(isAdjustedToUTC = *, unit = MICROS)
		// use ConvertedType TIME_MILLIS for TIME(isAdjustedToUTC = *, unit = MILLIS)
		TIME_NANOS,
		TIME_MICROS,
		TIME_MILLIS,
		
		// use ConvertedType TIMESTAMP_MICROS for TIMESTAMP(isAdjustedToUTC = *, unit = MICROS)
		// use ConvertedType TIMESTAMP_MILLIS for TIMESTAMP(isAdjustedToUTC = *, unit = MILLIS)
		TIMESTAMP_NANOS,
		TIMESTAMP_MICROS,
		TIMESTAMP_MILLIS,
		
		// 9: reserved for INTERVAL
		INTEGER, // use ConvertedType INT_* or UINT_*
		UNKNOWN, // no compatible ConvertedType
		JSON, // use ConvertedType JSON
		BSON, // use ConvertedType BSON
		UUID,
		
		NULL;
	}
	
	public abstract ParquetLogicalTypeEnum getTypeEnum();
	
	// ------------------------------------------------------------------------
	
	/** Empty public static classs to use as logical type annotations */
	public static class StringType extends ParquetLogicalType {  // allowed for BINARY, must be encoded with UTF-8
		public static final StringType INSTANCE = new StringType();
		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.BSON; }
	}
	public static class UUIDType extends ParquetLogicalType {    // allowed for FIXED[16], must encoded raw UUID bytes
		public static final UUIDType INSTANCE = new UUIDType();
		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.UUID; }
	}
	public static class MapType extends ParquetLogicalType {     // see LogicalTypes.md
		public static final MapType INSTANCE = new MapType();
		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.MAP; }
	}

	public static class ListType extends ParquetLogicalType {    // see LogicalTypes.md
		public static final ListType INSTANCE = new ListType();
		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.LIST; }
	}

	public static class EnumType extends ParquetLogicalType {    // allowed for BINARY, must be encoded with UTF-8
		public static final EnumType INSTANCE = new EnumType();
		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.ENUM; }
	}

	public static class DateType extends ParquetLogicalType {    // allowed for INT32
		public static final DateType INSTANCE = new DateType();
		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.DATE; }
	}


	/**
	 * Logical type to annotate a column that is always null.
	 *
	 * Sometimes when discovering the schema of existing data, values are always
	 * null and the physical type can't be determined. This annotation signals
	 * the case where the physical type was guessed from all null values.
	 */
	public static class NullType extends ParquetLogicalType {    // allowed for any physical type, only null values stored
		public static final NullType INSTANCE = new NullType();
		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.NULL; }
	}

	/**
	 * Decimal logical type annotation
	 *
	 * To maintain forward-compatibility in v1, implementations using this logical
	 * type must also set scale and precision on the annotated SchemaElement.
	 *
	 * Allowed for physical types: INT32, INT64, FIXED, and BINARY
	 */
	@Data @AllArgsConstructor
	public static class DecimalType extends ParquetLogicalType {
//		public static final DecimalType INSTANCE = new DecimalType();
		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.DECIMAL; }

		int scale;
		int precision;
	}

	/** Time units for logical types */
	public enum ParquetTimeUnit { MILLIS, MICROS, NANOS; }
	
	/**
	 * Time logical type annotation
	 *
	 * Allowed for physical types: INT32 (millis), INT64 (micros, nanos)
	 */
	@Data @AllArgsConstructor
	public static class TimeParquetLogicalType extends ParquetLogicalType {
		ParquetLogicalTypeEnum typeEnum;
		boolean isAdjustedToUTC;
		ParquetTimeUnit unit;
	}

//	public static abstract class TimeParquetLogicalType extends ParquetLogicalType {
//		boolean isAdjustedToUTC;
//		public abstract TimeUnit getUnit();
//	}
//	public static class MilliSecondsTimeParquetLogicalType extends TimeParquetLogicalType {
//		public static final MilliSecondsTimeParquetLogicalType INSTANCE = new MilliSecondsTimeParquetLogicalType();
//		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.TIMEMILLIS; }
//		@Override public TimeUnit getUnit() { return TimeUnit.MILLIS; }
//	}
//	public static class MicroSecondsTimeParquetLogicalType extends TimeParquetLogicalType {
//		public static final MicroSecondsTimeParquetLogicalType INSTANCE = new MicroSecondsTimeParquetLogicalType();
//		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.TIMEMICROS; }
//		@Override public TimeUnit getUnit() { return TimeUnit.MICROS; }
//	}
//	public static class NanoSecondsTimeParquetLogicalType extends TimeParquetLogicalType {
//		public static final NanoSecondsTimeParquetLogicalType INSTANCE = new NanoSecondsTimeParquetLogicalType();
//		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.TIMENANOS; }
//		@Override public TimeUnit getUnit() { return TimeUnit.NANOS; }
//	}

	/**
	 * Timestamp logical type annotation
	 *
	 * Allowed for physical types: INT64
	 */
	@Data @AllArgsConstructor
	public static class TimestampParquetLogicalType extends ParquetLogicalType {
		ParquetLogicalTypeEnum typeEnum;
		boolean isAdjustedToUTC;
		ParquetTimeUnit unit;
	}

//	public static abstract class TimestampParquetLogicalType extends ParquetLogicalType {
//		boolean isAdjustedToUTC;
//		public abstract ParquetTimeUnit getUnit();
//	}
//
//	public static class MillisTimestampParquetLogicalType extends TimestampParquetLogicalType {
//		public static final MillisTimestampParquetLogicalType INSTANCE = new MillisTimestampParquetLogicalType();
//		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.TIMESTAMPMILLIS; }
//		@Override public ParquetTimeUnit getUnit() { return ParquetTimeUnit.MILLIS; }
//	}
//	public static class MicrosTimestampParquetLogicalType extends TimestampParquetLogicalType {
//		public static final MicrosTimestampParquetLogicalType INSTANCE = new MicrosTimestampParquetLogicalType();
//		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.TIMESTAMPMICROS; }
//		@Override public ParquetTimeUnit getUnit() { return ParquetTimeUnit.MICROS; }
//	}
//	public static class NanosTimestampParquetLogicalType extends TimestampParquetLogicalType {
//		public static final NanosTimestampParquetLogicalType INSTANCE = new NanosTimestampParquetLogicalType();
//		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.TIMESTAMPNANOS; }
//		@Override public ParquetTimeUnit getUnit() { return ParquetTimeUnit.NANOS; }
//	}

	/**
	 * Integer logical type annotation
	 *
	 * bitWidth must be 8, 16, 32, or 64.
	 *
	 * Allowed for physical types: INT32, INT64
	 */
	@Data @AllArgsConstructor
	public static class IntType extends ParquetLogicalType {
		// public static final IntType INSTANCE = new IntType();
		long bitWidth;
		boolean isSigned;
		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.INTEGER; }
	}

	/**
	 * Embedded JSON logical type annotation
	 *
	 * Allowed for physical types: BINARY
	 */
	public static class JsonType extends ParquetLogicalType {
		public static final JsonType INSTANCE = new JsonType();
		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.JSON; }
	}

	/**
	 * Embedded BSON logical type annotation
	 *
	 * Allowed for physical types: BINARY
	 */
	public static class BsonType extends ParquetLogicalType {
		public static final BsonType INSTANCE = new BsonType();
		@Override public ParquetLogicalTypeEnum getTypeEnum() { return ParquetLogicalTypeEnum.BSON; }
	}
}
