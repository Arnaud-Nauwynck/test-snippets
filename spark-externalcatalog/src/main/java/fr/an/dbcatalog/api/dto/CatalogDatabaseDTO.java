package fr.an.dbcatalog.api.dto;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class CatalogDatabaseDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	String name;

	String description;

	URI locationUri;

	Map<String, String> properties;

	Map<String, CatalogTableDTO> tables = new HashMap<>();

	Map<String, CatalogFunctionDTO> functions = new HashMap<>();

}
