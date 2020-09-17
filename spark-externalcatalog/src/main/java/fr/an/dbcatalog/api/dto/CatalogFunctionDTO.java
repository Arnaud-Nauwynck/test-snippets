package fr.an.dbcatalog.api.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Value;

/**
 *
 */
public class CatalogFunctionDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	CatalogFunctionDTO.CatalogFunctionIdentifierDTO identifier;
    String className;
    List<CatalogFunctionDTO.CatalogFunctionResourceDTO> resources;

	@Value
	public static class CatalogFunctionIdentifierDTO implements Serializable {
		private static final long serialVersionUID = 1L;

		public final String database;
		public final String funcName;
    	
    }

	public static class CatalogFunctionResourceDTO implements Serializable {
		private static final long serialVersionUID = 1L;
		
		CatalogFunctionDTO.FunctionResourceTypeDTO resourceType;
		String uri;
	}
	
	public static enum FunctionResourceTypeDTO {
		jar,file, archive
	}

}