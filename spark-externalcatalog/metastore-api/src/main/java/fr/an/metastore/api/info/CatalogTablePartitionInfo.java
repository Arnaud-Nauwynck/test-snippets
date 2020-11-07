package fr.an.metastore.api.info;

import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogTableStatistics;
import fr.an.metastore.api.immutable.ImmutableCatalogTablePartitionDef;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CatalogTablePartitionInfo {

	public ImmutableCatalogTablePartitionDef def;
	
	// .. not in definition: 
	public long lastAccessTime;
	public ImmutableCatalogTableStatistics stats;

}
