package fr.an.dbcatalog.api.manager;

import java.util.List;

import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;
import org.apache.spark.sql.catalyst.expressions.Expression;

import fr.an.dbcatalog.api.PartitionSpec;
import fr.an.dbcatalog.api.exceptions.NoSuchPartitionRuntimeException;
import fr.an.dbcatalog.api.exceptions.PartitionAlreadyExistsRuntimeException;
import fr.an.dbcatalog.impl.model.DatabaseModel;
import fr.an.dbcatalog.impl.model.TableModel;
import fr.an.dbcatalog.impl.model.TablePartitionModel;
import fr.an.dbcatalog.impl.utils.ListUtils;
import lombok.val;

/**
 * part of AbstractJavaDbCatalog, for table partitions
 */
public abstract class DatabaseTablePartitionsLookup<TDb extends DatabaseModel,TTable extends TableModel,TPart extends TablePartitionModel> {

	public abstract TPart findPartition(TDb db, TTable table,
			PartitionSpec spec);

	public TPart getPartition(TDb db, TTable table,
			PartitionSpec spec) {
		val res = findPartition(db, table, spec);
		if (null == res) {
			String dbName = db.getName();
			String tableName = table.getTableName();
			throw new NoSuchPartitionRuntimeException(dbName, tableName, spec);
		}
		return res;
	}

	public List<TPart> getPartitions(TDb db, TTable table, List<PartitionSpec> partSpecs) {
		return ListUtils.map(partSpecs, x -> getPartition(db, table, x));
	}

	public List<TPart> getPartitionByDefs(TDb db, TTable table, List<CatalogTablePartition> partDefs) {
		return ListUtils.map(partDefs, x -> getPartition(db, table, PartitionSpec.fromScalaPartSpec(x.spec())));
	}
	
	public void requirePartitionsNotExist(TDb db, TTable table, List<PartitionSpec> partSpecs) {
		for (val partSpec : partSpecs) {
			if (null != findPartition(db, table, partSpec)) {
				String dbName = db.getName();
				String tableName = table.getTableName();
				throw new PartitionAlreadyExistsRuntimeException(dbName, tableName, partSpec);
			}
		}
	}

	public List<String> listPartitionNames(TDb db, TTable table,
			PartitionSpec spec) {
		val tmpres = listPartitions(db, table, spec);
		return ListUtils.map(tmpres, x -> x.getPartitionName());
	}

	public abstract List<TPart> listPartitions(TDb db, TTable table,
			PartitionSpec spec);

	public abstract List<TPart> listPartitionsByFilter(TDb db, TTable table,
			List<Expression> predicates, 
			String defaultTimeZoneId);


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
