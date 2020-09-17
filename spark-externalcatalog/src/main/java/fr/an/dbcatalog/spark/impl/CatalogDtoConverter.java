package fr.an.dbcatalog.spark.impl;

import java.net.URI;

import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;

import fr.an.dbcatalog.api.dto.CatalogDatabaseDTO;
import fr.an.dbcatalog.spark.util.ScalaCollUtils;

public class CatalogDtoConverter {


	public static CatalogDatabase toSparkCatalog(CatalogDatabaseDTO dto) {
		String name = dto.getName();
	    String description = dto.getDescription();
	    URI locationUri = dto.getLocationUri();
		java.util.Map<String, String> props = dto.getProperties();
		scala.collection.immutable.Map<String, String> scalaProps = ScalaCollUtils.toScalaImutableMap(props);

		return new CatalogDatabase(name, description, locationUri, scalaProps);
	}


}
