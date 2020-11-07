package fr.an.metastore.api.spi;

import java.util.List;

import fr.an.metastore.api.exceptions.CatalogRuntimeException;
import fr.an.metastore.impl.model.DatabaseModel;
import fr.an.metastore.impl.utils.MetastoreListUtils;

/**
 * part of AbstractJavaDbCatalog, for functions lookup by name
 */
public abstract class FunctionsLookup<TDb extends DatabaseModel,TFunc> {

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
		return MetastoreListUtils.filterPatternAndSort(all, pattern);
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
