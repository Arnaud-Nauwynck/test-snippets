package fr.an.metastore.api.manager;

import java.util.List;

import fr.an.metastore.api.dto.CatalogTablePartitionDTO;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import fr.an.metastore.impl.model.DatabaseModel;
import fr.an.metastore.impl.model.TableModel;
import fr.an.metastore.impl.model.TablePartitionModel;

/**
 * part of AbstractJavaDbCatalog, for table partitions DDL
 */
public abstract class TablePartitionsDDLManager<TDb extends DatabaseModel,TTable extends TableModel, TPart extends TablePartitionModel> {
	
	public abstract List<TPart> createPartitions(TDb db, TTable table, 
			List<CatalogTablePartitionDTO> parts,
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
			List<CatalogTablePartitionDTO> newPartDefs);

}
