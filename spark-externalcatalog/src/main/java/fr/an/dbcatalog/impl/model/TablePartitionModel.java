package fr.an.dbcatalog.impl.model;

import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;

import fr.an.dbcatalog.api.PartitionSpec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class TablePartitionModel {

	@Getter
	private final DatabaseModel db;
	@Getter
	private final TableModel table;
	
	@Getter
	private final PartitionSpec spec;

	@Getter
	private final String partitionName;
		
	
	@Getter @Setter
	CatalogTablePartition sparkDefinition;

	
}
