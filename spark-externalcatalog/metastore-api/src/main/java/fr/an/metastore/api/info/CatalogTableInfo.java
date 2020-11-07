package fr.an.metastore.api.info;

import fr.an.metastore.api.immutable.ImmutableCatalogTableDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogTableStatistics;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CatalogTableInfo {

	public final ImmutableCatalogTableDef def;
	
	public final long lastAccessTime;
	
	public final ImmutableCatalogTableStatistics stats;

}
