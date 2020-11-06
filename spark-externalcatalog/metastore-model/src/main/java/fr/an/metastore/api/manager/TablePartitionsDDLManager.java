package fr.an.metastore.api.manager;

import java.util.List;

import fr.an.metastore.api.immutable.ImmutableCatalogTablePartitionDef;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;

/**
 * part of AbstractJavaDbCatalog, for table partitions DDL
 */
public abstract class TablePartitionsDDLManager<TDb,TTable,TPart> {
	
	public abstract List<TPart> createPartitions(TDb db, TTable table, 
			List<ImmutableCatalogTablePartitionDef> parts,
			boolean ignoreIfExists);

	public abstract void dropPartitions(TDb db, TTable table,
			List<TPart> parts,
			boolean ignoreIfNotExists,
			boolean purge, boolean retainData);

	public abstract List<TPart> renamePartitions(TDb db, TTable table,
			List<TPart> parts,
			List<ImmutablePartitionSpec> newSpecs);

	public abstract void alterPartitions(TDb db, TTable table, 
			List<TPart> parts,
			List<ImmutableCatalogTablePartitionDef> newPartDefs);

}
