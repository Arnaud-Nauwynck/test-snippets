package fr.an.metastore.api.dto;

import java.io.Serializable;
import java.util.List;

import fr.an.metastore.api.immutable.FunctionResourceTypeEnum;
import lombok.Data;

/**
 *
 */
@Data
public class CatalogFunctionDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	CatalogFunctionIdentifierDTO identifier;
    
	String className;
    
    List<CatalogFunctionResourceDTO> resources;

	@Data
	public static class CatalogFunctionIdentifierDTO implements Serializable {
		private static final long serialVersionUID = 1L;

		String database;
		String funcName;
    	
    }

	@Data
	public static class CatalogFunctionResourceDTO implements Serializable {
		private static final long serialVersionUID = 1L;
		
		FunctionResourceTypeEnum resourceType;
		String uri;
	}

}