package fr.an.metastore.spark.modeladapter;

import java.net.URI;

import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;

import fr.an.metastore.impl.model.DatabaseModel;
import fr.an.metastore.spark.util.ScalaCollUtils;

public class SparkModelConverter {


	public static CatalogDatabase toSparkCatalog(DatabaseModel src) {
		String name = src.getName();
	    String description = src.getDescription();
	    URI locationUri = src.getLocationUri();
		java.util.Map<String, String> props = src.getProperties();
		scala.collection.immutable.Map<String, String> scalaProps = ScalaCollUtils.toScalaImutableMap(props);

		return new CatalogDatabase(name, description, locationUri, scalaProps);
	}


}
