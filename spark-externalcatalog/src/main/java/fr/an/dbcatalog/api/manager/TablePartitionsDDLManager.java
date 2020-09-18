package fr.an.dbcatalog.api.manager;

import java.util.List;

import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;

import fr.an.dbcatalog.api.PartitionSpec;
import fr.an.dbcatalog.impl.model.DatabaseModel;
import fr.an.dbcatalog.impl.model.TableModel;
import fr.an.dbcatalog.impl.model.TablePartitionModel;

/**
 * part of AbstractJavaDbCatalog, for table partitions DDL
 */
public abstract class TablePartitionsDDLManager<TDb extends DatabaseModel,TTable extends TableModel, TPart extends TablePartitionModel> {
	
	public abstract List<TPart> createPartitions(TDb db, TTable table, 
			List<CatalogTablePartition> parts,
			boolean ignoreIfExists);

	public abstract void dropPartitions(TDb db, TTable table,
			List<TPart> parts,
			boolean ignoreIfNotExists,
			boolean purge, boolean retainData);

	public abstract List<TPart> renamePartitions(TDb db, TTable table,
			List<TPart> parts,
			List<PartitionSpec> newSpecs);

	public abstract void alterPartitions(TDb db, TTable table, 
			List<TPart> parts,
			List<CatalogTablePartition> newPartDefs);

}
