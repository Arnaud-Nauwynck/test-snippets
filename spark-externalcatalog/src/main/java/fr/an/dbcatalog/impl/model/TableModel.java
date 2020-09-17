package fr.an.dbcatalog.impl.model;

import org.apache.spark.sql.catalyst.catalog.CatalogTable;

import lombok.Getter;
import lombok.Setter;

public class TableModel {

	@Getter
	private final DatabaseModel db;
	@Getter
	private final String tableName;
	
	@Getter @Setter
	CatalogTable sparkTableDefinition;
	
	public TableModel(DatabaseModel db, String tableName, CatalogTable sparkTableDefinition) {
		this.db = db;
		this.tableName = tableName;
		this.sparkTableDefinition = sparkTableDefinition;
	}

}
