package fr.an.metastore.api.immutable;

import org.apache.avro.Schema;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;

@Data @RequiredArgsConstructor
public class ImmutableStructType {
	
	private final ImmutableList<ImmutableStructField> fields;

	@Getter @Setter
	private Schema asAvroSchema;
	@Getter @Setter
	private Object asSparkStruct;
	
	@Value @AllArgsConstructor
	public static class ImmutableStructField {

		public final String name;
		public final ImmutableDataType dataType;
	    public final boolean nullable;
	    public final ImmutableSortedMap<String,String> data;
	}
	
    @Value
    public static class ImmutableDataType {
    	public final String typeName;
		// TODO struct => recursive DataType ..
    }
    
}
