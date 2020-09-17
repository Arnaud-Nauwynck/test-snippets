package fr.an.dbcatalog.impl.model;

import org.apache.spark.sql.catalyst.catalog.CatalogFunction;

import lombok.Getter;
import lombok.Setter;

public class CatalogFunctionModel {
	
	@Getter @Setter
	CatalogFunction sparkFunctionDef;
	
	public String getFuncName() {
		return sparkFunctionDef.identifier().funcName();
	}
	
}
