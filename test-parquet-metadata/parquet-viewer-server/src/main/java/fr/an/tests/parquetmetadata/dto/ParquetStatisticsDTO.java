package fr.an.tests.parquetmetadata.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeName;

import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO.BinaryParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO.BooleanParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO.DoubleParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO.FloatParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO.IntParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO.LongParquetStatisticsDTO;
import fr.an.tests.parquetmetadata.dto.ParquetStatisticsDTO.StringParquetStatisticsDTO;
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
//	 * DEPRECATED: min and max value of the column. Use min_value and max_value.
//	 *
//	 * Values are encoded using PLAIN encoding, except that variable-length byte
//	 * arrays do not include a length prefix.
//	 *
//	 * These fields encode min and max values determined by signed comparison only.
//	 * New files should use the correct order for a column's logical type and store
//	 * the values in the min_value and max_value fields.
//	 *
//	 * To support older readers, these may be set when the column order is signed.
//	 */
//	byte[] max;
//	byte[] min;

	/** count of null value in the column */
	Long null_count;
	
	/** count of distinct values occurring */ // TODO not in java class??
	@JsonInclude(Include.NON_NULL)
	Long distinct_count;
	
//	/**
//	 * Min and max values for the column, determined by its ColumnOrder.
//	 *
//	 * Values are encoded using PLAIN encoding, except that variable-length byte
//	 * arrays do not include a length prefix.
//	 */
//	public abstract byte[] max_value();
//	public abstract byte[] min_value();

	@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
	@JsonTypeName("Binary")
	public static class BinaryParquetStatisticsDTO extends ParquetStatisticsDTO<byte[]> {
		@Override
		public String getType() { return "Binary"; }
		
		public BinaryParquetStatisticsDTO(Long null_count, Long distinct_count, byte[] min_value, byte[] max_value) {
			super(null_count, distinct_count);
			this.min_value = min_value;
			this.max_value = max_value;
		}

		public byte[] min_value;
		public byte[] max_value;
	}

	// cf for Binary..
	@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
	@JsonTypeName("String")
	public static class StringParquetStatisticsDTO extends ParquetStatisticsDTO<String> {
		
		public StringParquetStatisticsDTO(Long null_count, Long distinct_count, String min_value, String max_value) {
			super(null_count, distinct_count);
			this.min_value = min_value;
			this.max_value = max_value;
		}
		@Override
		public String getType() { return "String"; }
		
		public String min_value;
		public String max_value;
	}

	@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
	@JsonTypeName("Boolean")
	public static class BooleanParquetStatisticsDTO extends ParquetStatisticsDTO<Boolean> {
		
		public BooleanParquetStatisticsDTO(Long null_count, Long distinct_count, boolean min_value, boolean max_value) {
			super(null_count, distinct_count);
			this.min_value = min_value;
			this.max_value = max_value;
		}

		@Override
		public String getType() { return "Boolean"; }

		public boolean min_value;
		public boolean max_value;
	}

	@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
	@JsonTypeName("Double")
	public static class DoubleParquetStatisticsDTO extends ParquetStatisticsDTO<Double> {
		
		public DoubleParquetStatisticsDTO(Long null_count, Long distinct_count, double min_value, double max_value) {
			super(null_count, distinct_count);
			this.min_value = min_value;
			this.max_value = max_value;
		}
		
		@Override
		public String getType() { return "Double"; }
		
		public double min_value;
		public double max_value;
	}

	@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
	@JsonTypeName("Float")
	public static class FloatParquetStatisticsDTO extends ParquetStatisticsDTO<Float> {
		
		public FloatParquetStatisticsDTO(Long null_count, Long distinct_count, float min_value, float max_value) {
			super(null_count, distinct_count);
			this.min_value = min_value;
			this.max_value = max_value;
		}
		
		@Override
		public String getType() { return "Float"; }
		
		public float min_value;
		public float max_value;
	}

	@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
	@JsonTypeName("Int")
	public static class IntParquetStatisticsDTO extends ParquetStatisticsDTO<Integer> {
		
		public IntParquetStatisticsDTO(Long null_count, Long distinct_count, int min_value, int max_value) {
			super(null_count, distinct_count);
			this.min_value = min_value;
			this.max_value = max_value;
		}
		@Override
		public String getType() { return "Int"; }
		
		public int min_value;
		public int max_value;
	}
	
	@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
	@JsonTypeName("Long")
	public static class LongParquetStatisticsDTO extends ParquetStatisticsDTO<Long> {
		
		public LongParquetStatisticsDTO(Long null_count, Long distinct_count, long min_value, long max_value) {
			super(null_count, distinct_count);
			this.min_value = min_value;
			this.max_value = max_value;
		}
		
		@Override
		public String getType() { return "Long"; }
		
		public long min_value;
		public long max_value;
	}
	
}