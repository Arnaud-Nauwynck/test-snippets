package fr.an.metastore.api.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.Data;

@Data
public class StructTypeDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<StructFieldDTO> fields;
	
	@Data
	public static class StructFieldDTO implements Serializable {

		private static final long serialVersionUID = 1L;

		String name;
		DataTypeDTO dataType;
	    boolean nullable = true;
	    MetadataDTO metadata = new MetadataDTO();
	}
	
    @Data
    public static class DataTypeDTO implements Serializable {
    	
		private static final long serialVersionUID = 1L;

		// TODO
    }
    
    public static class MetadataDTO extends LinkedHashMap<String,String> implements Serializable {

		private static final long serialVersionUID = 1L;

    }
}
