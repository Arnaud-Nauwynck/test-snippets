package fr.an.dbcatalog.api.manager;

import java.util.List;

import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;

/**
 * part of AbstractJavaDbCatalog, for databases by name
 */
public abstract class DatabasesManager<TDb> {

	public abstract void setCurrentDatabase(String db);
	
	public abstract void createDatabase(CatalogDatabase dbDefinition, boolean ignoreIfExists);

	public abstract void dropDatabase(TDb db, boolean ignoreIfNotExists, boolean cascade);

	public abstract void alterDatabase(TDb db, CatalogDatabase dbDefinition);

	public abstract CatalogDatabase getDatabase(String db);

	public abstract boolean databaseExists(String db);

	public abstract List<String> listDatabases();

	public abstract List<String> listDatabases(String pattern);

}
