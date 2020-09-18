package fr.an.dbcatalog.impl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.expressions.Expression;

import fr.an.dbcatalog.api.PartitionSpec;
import lombok.Getter;
import lombok.Setter;

public class TableModel {

	@Getter
	private final DatabaseModel db;
	@Getter
	private final String tableName;
	
	@Getter @Setter
	private CatalogTable sparkTableDefinition;
	
	Map<PartitionSpec, TablePartitionModel> partitions = new HashMap<>();
	
	// --------------------------------------------------------------------------------------------

	public TableModel(DatabaseModel db, String tableName, CatalogTable sparkTableDefinition) {
		this.db = db;
		this.tableName = tableName;
		this.sparkTableDefinition = sparkTableDefinition;
	}

	// --------------------------------------------------------------------------------------------

	public TablePartitionModel findPartition(PartitionSpec spec) {
		return partitions.get(spec);
	}

	public List<TablePartitionModel> listPartitions(PartitionSpec partialSpec) {
		if (partialSpec == null || partialSpec.isEmpty()) {
			return new ArrayList<>(partitions.values());
		}
		// TODO
		throw new UnsupportedOperationException("TODO NOT IMPLEMENTED YET");
	}

	public List<TablePartitionModel> listPartitionsByFilter(
			List<Expression> predicates, String defaultTimeZoneId) {
		// TODO
		throw new UnsupportedOperationException("TODO NOT IMPLEMENTED YET");
	}

	public void addPartition(TablePartitionModel part) {
		partitions.put(part.getSpec(), part);
	}

	public void removePartition(TablePartitionModel part) {
		partitions.remove(part.getSpec());
	}
	
}
