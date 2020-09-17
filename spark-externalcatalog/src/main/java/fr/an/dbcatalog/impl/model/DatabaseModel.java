package fr.an.dbcatalog.impl.model;

import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;

import lombok.Getter;
import lombok.Setter;

public class DatabaseModel {

	@Getter @Setter
	CatalogDatabase sparkDbDefinition;

	public String getName() {
		return sparkDbDefinition.name();
	}
	
//	  scala.collection.mutable.HashMap<String, TableDesc> tables = new scala.collection.mutable.HashMap<String, TableDesc>();
//	  scala.collection.mutable.HashMap<String, CatalogFunction> functions = new scala.collection.mutable.HashMap<String, CatalogFunction>();
//
//	  public TableDesc getTable(String table) {
//		  Option<TableDesc> opt = tables.get(table);
//		  if (opt.isEmpty()) {
//			  throw new CatalogRuntimeException("No such table '" + db.name() + "." + table + "'");
//		  }
//		  return opt.get();
//	  }
//
//	  public void requireFunctionExists(String funcName) {
//		  getFunction(funcName);
//	  }
//	  public void requireFunctionNotExists(String funcName) {
//    	  throw new CatalogRuntimeException("Function already exists '" + db.name() + "." + funcName + "'");
//	  }
//	  
//	  private CatalogFunction getFunction(String funcName) {
//		  Option<CatalogFunction> opt = functions.get(funcName);
//		  if (opt.isEmpty()) {
//			  throw new CatalogRuntimeException("No such function '" + db.name() + "." + funcName + "'");
//		  }
//		  return opt.get();
//	  }


}
