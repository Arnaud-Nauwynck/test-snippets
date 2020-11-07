package fr.an.metastore.api.spi;

import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;

public interface IDatabaseModel {

	String getName();

	ImmutableCatalogDatabaseDef getDbDef();

	
}
