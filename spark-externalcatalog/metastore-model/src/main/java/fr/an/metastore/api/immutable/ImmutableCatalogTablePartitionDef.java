package fr.an.metastore.api.immutable;

import com.google.common.collect.ImmutableMap;

import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogStorageFormat;
import lombok.Builder;
import lombok.Value;

@Value @Builder
public class ImmutableCatalogTablePartitionDef {
	
	public final ImmutablePartitionSpec spec;
	
	public final ImmutableCatalogStorageFormat storage;
	
	public final ImmutableMap<String,String> parameters;
	
	public final long createTime;
	// .. not in definition: 
	// public long lastAccessTime;
	// public ImmutableCatalogStatistics stats;

	private final String partitionName;

}
