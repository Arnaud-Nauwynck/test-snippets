package fr.an.spark.externalcatalog.impl;


import java.net.URI;

import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;

import fr.an.spark.externalcatalog.dto.CatalogDatabaseDTO;
import scala.Predef;
import scala.Tuple2;
import scala.collection.JavaConverters;

public class SparkCatalogHelper {

	public static <A, B> scala.collection.mutable.Map<A, B> toScalaMutableMap(java.util.Map<A, B> m) {
	    return JavaConverters.mapAsScalaMap(m);
	}

	public static <A, B> scala.collection.immutable.Map<A, B> toScalaImutableMap(java.util.Map<A, B> m) {
	    return JavaConverters.mapAsScalaMapConverter(m).asScala().toMap(
	      Predef.<Tuple2<A, B>>$conforms()
	    );
	  }
	
	public static CatalogDatabase toSparkCatalog(CatalogDatabaseDTO dto) {
		String name = dto.getName();
	    String description = dto.getDescription();
	    URI locationUri = dto.getLocationUri();
		java.util.Map<String, String> props = dto.getProperties();
		scala.collection.immutable.Map<String, String> scalaProps = toScalaImutableMap(props);

		return new CatalogDatabase(name, description, locationUri, scalaProps);
	}

}
