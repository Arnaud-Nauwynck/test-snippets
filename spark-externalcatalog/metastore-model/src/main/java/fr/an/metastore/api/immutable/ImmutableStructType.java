package fr.an.metastore.api.immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;

import fr.an.metastore.api.dto.StructTypeDTO.DataTypeDTO;
import fr.an.metastore.api.dto.StructTypeDTO.StructFieldDTO;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value @AllArgsConstructor
public class ImmutableStructType {

	private ImmutableList<StructFieldDTO> fields;
	
	@Value @AllArgsConstructor
	public static class ImmutableStructField {

		public final String name;
		public final DataTypeDTO dataType;
	    public final boolean nullable = true;
	    public final ImmutableSortedMap<String,String> data;
	}
	
    @Value
    public static class ImmutableDataType {
    	
		// TODO
    }
    
}
