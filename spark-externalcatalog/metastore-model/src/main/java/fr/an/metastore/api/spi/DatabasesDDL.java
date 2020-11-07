package fr.an.metastore.api.spi;

import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;

/**
 * part of AbstractJavaDbCatalog, for databases DDL
 */
public abstract class DatabasesDDL<TDb> {

	public abstract TDb createDatabase(String dbName, ImmutableCatalogDatabaseDef dbDef, boolean ignoreIfExists);

	public abstract void dropDatabase(TDb db, boolean ignoreIfNotExists, boolean cascade);

	public abstract void alterDatabase(TDb db, ImmutableCatalogDatabaseDef dbDef);

}
