package fr.an.tests.parquetmetadata.dto;

/**
 * LogicalType annotations to replace ConvertedType.
 *
 * To maintain compatibility, implementations using LogicalType for a
 * SchemaElement must also set the corresponding ConvertedType from the
 * following table.
 */
public class ParquetLogicalType {

	// Union ....

	StringType STRING; // use ConvertedType UTF8
	MapType MAP; // use ConvertedType MAP
	ListType LIST; // use ConvertedType LIST
	EnumType ENUM; // use ConvertedType ENUM
	DecimalType DECIMAL; // use ConvertedType DECIMAL
	DateType DATE; // use ConvertedType DATE

	// use ConvertedType TIME_MICROS for TIME(isAdjustedToUTC = *, unit = MICROS)
	// use ConvertedType TIME_MILLIS for TIME(isAdjustedToUTC = *, unit = MILLIS)
	TimeType TIME;

	// use ConvertedType TIMESTAMP_MICROS for TIMESTAMP(isAdjustedToUTC = *, unit =
	// MICROS)
	// use ConvertedType TIMESTAMP_MILLIS for TIMESTAMP(isAdjustedToUTC = *, unit =
	// MILLIS)
	TimestampType TIMESTAMP;

	// 9: reserved for INTERVAL
	IntType INTEGER; // use ConvertedType INT_* or UINT_*
	NullType UNKNOWN; // no compatible ConvertedType
	JsonType JSON; // use ConvertedType JSON
	BsonType BSON; // use ConvertedType BSON
	UUIDType UUID;
	
	
	// ------------------------------------------------------------------------
	
	/** Empty public static classs to use as logical type annotations */
	public static class StringType {}  // allowed for BINARY, must be encoded with UTF-8
	public static class UUIDType {}    // allowed for FIXED[16], must encoded raw UUID bytes
	public static class MapType {}     // see LogicalTypes.md
	public static class ListType {}    // see LogicalTypes.md
	public static class EnumType {}    // allowed for BINARY, must be encoded with UTF-8
	public static class DateType {}    // allowed for INT32

	/**
	 * Logical type to annotate a column that is always null.
	 *
	 * Sometimes when discovering the schema of existing data, values are always
	 * null and the physical type can't be determined. This annotation signals
	 * the case where the physical type was guessed from all null values.
	 */
	public static class NullType {}    // allowed for any physical type, only null values stored

	/**
	 * Decimal logical type annotation
	 *
	 * To maintain forward-compatibility in v1, implementations using this logical
	 * type must also set scale and precision on the annotated SchemaElement.
	 *
	 * Allowed for physical types: INT32, INT64, FIXED, and BINARY
	 */
	public static class DecimalType {
	  int scale;
	  int precision;
	}

	/** Time units for logical types */
	public static class MilliSeconds {}
	public static class MicroSeconds {}
	public static class NanoSeconds {}
	public static class TimeUnit {
		// union..
		MilliSeconds MILLIS;
		MicroSeconds MICROS;
		NanoSeconds NANOS;
	}

	/**
	 * Timestamp logical type annotation
	 *
	 * Allowed for physical types: INT64
	 */
	public static class TimestampType {
		boolean isAdjustedToUTC;
		TimeUnit unit;
	}

	/**
	 * Time logical type annotation
	 *
	 * Allowed for physical types: INT32 (millis), INT64 (micros, nanos)
	 */
	public static class TimeType {
	  boolean isAdjustedToUTC;
	  TimeUnit unit;
	}

	/**
	 * Integer logical type annotation
	 *
	 * bitWidth must be 8, 16, 32, or 64.
	 *
	 * Allowed for physical types: INT32, INT64
	 */
	public static class IntType {
	  long bitWidth;
	  boolean isSigned;
	}

	/**
	 * Embedded JSON logical type annotation
	 *
	 * Allowed for physical types: BINARY
	 */
	public static class JsonType {
	}

	/**
	 * Embedded BSON logical type annotation
	 *
	 * Allowed for physical types: BINARY
	 */
	public static class BsonType {
	}
}
