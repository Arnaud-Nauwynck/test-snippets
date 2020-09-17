package fr.an.dbcatalog.api.manager;

import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;

/**
 * part of AbstractJavaDbCatalog, for databases DDL
 */
public abstract class DatabasesDDLManager<TDb> {

	public abstract TDb createDatabase(CatalogDatabase dbDefinition, boolean ignoreIfExists);

	public abstract void dropDatabase(TDb db, boolean ignoreIfNotExists, boolean cascade);

	public abstract void alterDatabase(TDb db, CatalogDatabase dbDefinition);

}
