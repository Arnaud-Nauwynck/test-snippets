package fr.an.tests.parquetmetadata.dto.parquet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import fr.an.tests.parquetmetadata.dto.parquet.ParquetStatisticsDTO.BinaryParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetStatisticsDTO.BooleanParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetStatisticsDTO.DoubleParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetStatisticsDTO.FloatParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetStatisticsDTO.IntParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetStatisticsDTO.LongParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.parquet.ParquetStatisticsDTO.StringParquetStatisticsDTO;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Statistics per row group and per page All fields are optional.
 */
@Data @NoArgsConstructor @AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes(value = { 
		@JsonSubTypes.Type(BinaryParquetStatisticsDTO.class), 
		@JsonSubTypes.Type(StringParquetStatisticsDTO.class), 
		@JsonSubTypes.Type(BooleanParquetStatisticsDTO.class), 
		@JsonSubTypes.Type(DoubleParquetStatisticsDTO.class), 
		@JsonSubTypes.Type(FloatParquetStatisticsDTO.class),
		@JsonSubTypes.Type(LongParquetStatisticsDTO.class), 
		@JsonSubTypes.Type(IntParquetStatisticsDTO.class)
	}) 
public abstract class ParquetStatisticsDTO<T> {

	@JsonIgnore // cf @JsonTypeInfo
	public abstract String getType();
	
//	/**
//	 * DEPRECATED: min and max value of the column. Use minValue and maxValue.
//	 *
//	 * Values are encoded using PLAIN encoding, except that variable-length byte
//	 * arrays do not include a length prefix.
//	 *
//	 * These fields encode min and max values determined by signed comparison only.
//	 * New files should use the correct order for a column's logical type and store
//	 * the values in the minValue and maxValue fields.
//	 *
//	 * To support older readers, these may be set when the column order is signed.
//	 */
//	byte[] max;
//	byte[] min;

	/** count of null value in the column */
	Long nullCount;
	
	/** count of distinct values occurring */ // TODO not in java class??
	@JsonInclude(Include.NON_NULL)
	Long distinctCount;
	
//	/**
//	 * Min and max values for the column, determined by its ColumnOrder.
//	 *
//	 * Values are encoded using PLAIN encoding, except that variable-length byte
//	 * arrays do not include a length prefix.
//	 */
//	public abstract byte[] maxValue();
//	public abstract byte[] minValue();

	@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
	@JsonTypeName("Binary")
	public static class BinaryParquetStatisticsDTO extends ParquetStatisticsDTO<byte[]> {
		@Override
		public String getType() { return "Binary"; }
		
		public BinaryParquetStatisticsDTO(Long nullCount, Long distinctCount, byte[] minValue, byte[] maxValue) {
			super(nullCount, distinctCount);
			this.minValue = minValue;
			this.maxValue = maxValue;
		}

		public byte[] minValue;
		public byte[] maxValue;
	}

	// cf for Binary..
	@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
	@JsonTypeName("String")
	public static class StringParquetStatisticsDTO extends ParquetStatisticsDTO<String> {
		
		public StringParquetStatisticsDTO(Long nullCount, Long distinctCount, String minValue, String maxValue) {
			super(nullCount, distinctCount);
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		@Override
		public String getType() { return "String"; }
		
		public String minValue;
		public String maxValue;
	}

	@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
	@JsonTypeName("Boolean")
	public static class BooleanParquetStatisticsDTO extends ParquetStatisticsDTO<Boolean> {
		
		public BooleanParquetStatisticsDTO(Long nullCount, Long distinctCount, boolean minValue, boolean maxValue) {
			super(nullCount, distinctCount);
			this.minValue = minValue;
			this.maxValue = maxValue;
		}

		@Override
		public String getType() { return "Boolean"; }

		public boolean minValue;
		public boolean maxValue;
	}

	@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
	@JsonTypeName("Double")
	public static class DoubleParquetStatisticsDTO extends ParquetStatisticsDTO<Double> {
		
		public DoubleParquetStatisticsDTO(Long nullCount, Long distinctCount, double minValue, double maxValue) {
			super(nullCount, distinctCount);
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		
		@Override
		public String getType() { return "Double"; }
		
		public double minValue;
		public double maxValue;
	}

	@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
	@JsonTypeName("Float")
	public static class FloatParquetStatisticsDTO extends ParquetStatisticsDTO<Float> {
		
		public FloatParquetStatisticsDTO(Long nullCount, Long distinctCount, float minValue, float maxValue) {
			super(nullCount, distinctCount);
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		
		@Override
		public String getType() { return "Float"; }
		
		public float minValue;
		public float maxValue;
	}

	@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
	@JsonTypeName("Int")
	public static class IntParquetStatisticsDTO extends ParquetStatisticsDTO<Integer> {
		
		public IntParquetStatisticsDTO(Long nullCount, Long distinctCount, int minValue, int maxValue) {
			super(nullCount, distinctCount);
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		@Override
		public String getType() { return "Int"; }
		
		public int minValue;
		public int maxValue;
	}
	
	@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
	@JsonTypeName("Long")
	public static class LongParquetStatisticsDTO extends ParquetStatisticsDTO<Long> {
		
		public LongParquetStatisticsDTO(Long nullCount, Long distinctCount, long minValue, long maxValue) {
			super(nullCount, distinctCount);
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		
		@Override
		public String getType() { return "Long"; }
		
		public long minValue;
		public long maxValue;
	}
	
}