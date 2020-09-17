package fr.an.dbcatalog.impl.model;

import org.apache.spark.sql.catalyst.catalog.CatalogFunction;

import lombok.Getter;
import lombok.Setter;

public class FunctionModel {

	@Getter
	private final DatabaseModel db;
	@Getter
	private final String funcName;
	
	@Getter @Setter
	CatalogFunction sparkFunctionDefinition;
	
	public FunctionModel(DatabaseModel db, String funcName, CatalogFunction sparkFunctionDefinition) {
		this.db = db;
		this.funcName = funcName;
		this.sparkFunctionDefinition = sparkFunctionDefinition;
	}

}
