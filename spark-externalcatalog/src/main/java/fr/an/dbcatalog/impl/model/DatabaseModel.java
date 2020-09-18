package fr.an.dbcatalog.impl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;

import lombok.Getter;
import lombok.Setter;

public class DatabaseModel {

	@Getter
	private final String name;
	
	@Getter @Setter
	CatalogDatabase sparkDbDefinition;

	private final Map<String,TableModel> tables = new HashMap<>();

	private final Map<String,FunctionModel> functions = new HashMap<>();

	// --------------------------------------------------------------------------------------------

	public DatabaseModel(String name, CatalogDatabase sparkDbDefinition) {
		this.name = name;
		this.sparkDbDefinition = sparkDbDefinition;
	}

	// --------------------------------------------------------------------------------------------

	public TableModel findTable(String tableName) {
		return tables.get(tableName);
	}

	public List<String> listTables() {
		return new ArrayList<>(tables.keySet());
	}

	public void addTable(TableModel tbl) {
		tables.put(tbl.getTableName(), tbl);
	}

	public void removeTable(TableModel tbl) {
		tables.remove(tbl.getTableName());
	}

	public FunctionModel findFunction(String funcName) {
		return functions.get(funcName);
	}

	public List<String> listFunctions() {
		return new ArrayList<>(functions.keySet());
	}

	public void addFunction(FunctionModel func) {
		functions.put(func.getFuncName(), func);
	}

	public void removeFunction(FunctionModel func) {
		functions.remove(func.getFuncName());
	}
	
}
