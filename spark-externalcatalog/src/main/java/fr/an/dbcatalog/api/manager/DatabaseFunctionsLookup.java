package fr.an.dbcatalog.api.manager;

import java.util.List;

import fr.an.dbcatalog.api.exceptions.CatalogRuntimeException;
import fr.an.dbcatalog.impl.model.DatabaseModel;
import fr.an.dbcatalog.spark.util.SparkPatternUtils;

/**
 * part of AbstractJavaDbCatalog, for functions lookup by name
 */
public abstract class DatabaseFunctionsLookup<TDb extends DatabaseModel,TFunc> {

	public abstract TFunc findFunction(TDb db, String funcName);

	public TFunc getFunction(TDb db, String funcName) {
		TFunc res = findFunction(db, funcName);
		if (null == res) {
			String dbName = db.getName();
			throw new CatalogRuntimeException("Function '" + dbName + "." + funcName + "' not found");
		}
		return res;
	}

	public void requireFunctionNotExists(TDb db, String funcName) {
		TFunc found = findFunction(db, funcName);
		if (null != found) {
			String dbName = db.getName();
			throw new CatalogRuntimeException("Function '" + dbName + "." + funcName + "' already exists");
		}
	}

	public boolean functionExists(TDb db, String funcName) {
		TFunc res = findFunction(db, funcName);
		return null != res;
	}

	public abstract List<String> listFunctions(TDb db);

	public List<String> listFunctions(TDb db, String pattern) {
		List<String> all = listFunctions(db);
		return SparkPatternUtils.filterPatternAndSort(all, pattern);
	}

	public boolean hasFunction(TDb db) {
		return !listFunctions(db).isEmpty();
	}

	public abstract void add(TFunc func);
	public abstract void remove (TFunc func);

	public void removeAdd(TFunc oldFunc, TFunc newFunc) {
		remove(oldFunc);
		add(newFunc);
	}

}
