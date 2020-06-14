package fr.an.spark.externalcatalog.dto;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class CatalogDatabaseDTO implements Serializable {

	String name;

	String description;

	URI locationUri;

	Map<String, String> properties;

	Map<String, CatalogTableDescDTO> tables = new HashMap<>();

	Map<String, CatalogFunctionDTO> functions = new HashMap<>();

	/**
	 *
	 */
	public static class CatalogTableDescDTO implements Serializable {

	}

	/**
	 *
	 */
	public static class CatalogFunctionDTO implements Serializable {

	}

}
