package fr.an.metastore.api.spi;

import java.util.List;

import fr.an.metastore.api.dto.CatalogTablePartitionDTO;
import fr.an.metastore.api.exceptions.NoSuchPartitionRuntimeException;
import fr.an.metastore.api.exceptions.PartitionAlreadyExistsRuntimeException;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import fr.an.metastore.impl.model.DatabaseModel;
import fr.an.metastore.impl.model.TableModel;
import fr.an.metastore.impl.model.TablePartitionModel;
import fr.an.metastore.impl.utils.MetastoreListUtils;
import lombok.val;

/**
 * part of AbstractJavaDbCatalog, for table partitions
 */
public abstract class TablePartitionsLookup<TDb extends DatabaseModel,TTable extends TableModel,TPart extends TablePartitionModel> {

	public abstract TPart findPartition(TDb db, TTable table,
			ImmutablePartitionSpec spec);

	public TPart getPartition(TDb db, TTable table,
			ImmutablePartitionSpec spec) {
		val res = findPartition(db, table, spec);
		if (null == res) {
			String dbName = db.getName();
			String tableName = table.getTableName();
			throw new NoSuchPartitionRuntimeException(dbName, tableName, spec);
		}
		return res;
	}

	public static List<ImmutablePartitionSpec> toPartitionSpecs(List<CatalogTablePartitionDTO> parts) {
		return MetastoreListUtils.map(parts, x -> x.getSpec());
	}

	public List<TPart> getPartitions(TDb db, TTable table, List<ImmutablePartitionSpec> partSpecs) {
		return MetastoreListUtils.map(partSpecs, x -> getPartition(db, table, x));
	}

	public List<TPart> getPartitionByDefs(TDb db, TTable table, List<CatalogTablePartitionDTO> parts) {
		return getPartitions(db, table, toPartitionSpecs(parts));
	}

	public void requirePartitionDtosNotExist(TDb db, TTable table, List<CatalogTablePartitionDTO> tableParts) {
		requirePartitionsNotExist(db, table, toPartitionSpecs(tableParts));
	}

	public void requirePartitionsNotExist(TDb db, TTable table, List<ImmutablePartitionSpec> partSpecs) {
		for (val partSpec : partSpecs) {
			if (null != findPartition(db, table, partSpec)) {
				String dbName = db.getName();
				String tableName = table.getTableName();
				throw new PartitionAlreadyExistsRuntimeException(dbName, tableName, partSpec);
			}
		}
	}

	public List<String> listPartitionNamesByPartialSpec(TDb db, TTable table,
			ImmutablePartitionSpec partialSpec) {
		val tmpres = listPartitionsByPartialSpec(db, table, partialSpec);
		return MetastoreListUtils.map(tmpres, x -> x.getPartitionName());
	}

	public abstract List<TPart> listPartitionsByPartialSpec(TDb db, TTable table,
			ImmutablePartitionSpec spec);

//	public abstract List<TPart> listPartitionsByFilter(TDb db, TTable table,
//			List<ExpressionDTO> predicates, 
//			String defaultTimeZoneId);


	public abstract void addPartition(TPart part);
	public abstract void removePartition(TPart part);

	public void addPartitions(List<TPart> parts) {
		for(TPart p : parts) {
			addPartition(p);
		}
	}

	public void removePartitions(List<TPart> parts) {
		for(TPart p : parts) {
			removePartition(p);
		}
	}

	public void removeAddPartitions(
			List<TPart> oldPartitionModels,
			List<TPart> newPartitionModels) {
		removePartitions(oldPartitionModels);
		addPartitions(newPartitionModels);
	}

}
