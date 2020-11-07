package fr.an.metastore.api.spi;

import fr.an.metastore.api.immutable.ImmutableCatalogTableDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogTableStatistics;

public interface ITableModel {

	String getTableName();

	ImmutableCatalogTableDef getDef();

	long getLastAccessTime();

	ImmutableCatalogTableStatistics getStats();

}
