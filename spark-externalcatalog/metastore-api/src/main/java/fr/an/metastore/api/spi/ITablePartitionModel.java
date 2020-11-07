package fr.an.metastore.api.spi;

import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogTableStatistics;
import fr.an.metastore.api.immutable.ImmutableCatalogTablePartitionDef;

public interface ITablePartitionModel {

	String getPartitionName();

	ImmutableCatalogTablePartitionDef getDef();

	long getLastAccessTime();

	ImmutableCatalogTableStatistics getStats();

}
