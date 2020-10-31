package fr.an.metastore.api.manager;

import java.util.List;

import fr.an.metastore.api.exceptions.CatalogRuntimeException;
import fr.an.metastore.impl.utils.MetastoreListUtils;

/**
 * part of AbstractJavaDbCatalog, for databases lookups by name
 */
public abstract class DatabasesLookup<TDb> {

	public abstract TDb findDatabase(String db);

	public TDb getDatabase(String db) {
		TDb res = findDatabase(db);
		if (null == res) {
			throw new CatalogRuntimeException("Database '" + db + "' not found");
		}
		return res;
	}

	public abstract void addDatabase(TDb db);
	public abstract void removeDatabase(TDb db);
	
	public boolean databaseExists(String db) {
		TDb res = findDatabase(db);
		return res != null;
	}

	public abstract List<String> listDatabases();

	public List<String> listDatabases(String pattern) {
		List<String> all = listDatabases();
		return MetastoreListUtils.filterPatternAndSort(all, pattern);
	}
	
}
