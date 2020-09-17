package fr.an.dbcatalog.api.manager;

import java.util.List;

import fr.an.dbcatalog.api.exceptions.CatalogRuntimeException;
import fr.an.dbcatalog.impl.model.DatabaseModel;
import fr.an.dbcatalog.impl.utils.ListUtils;
import fr.an.dbcatalog.spark.util.SparkPatternUtils;
import lombok.val;

/**
 * part of AbstractJavaDbCatalog, for table lookup by name (on database)
 *
 */
public abstract class DatabaseTablesLookup<TDb extends DatabaseModel,TTable> {

	public abstract TTable findTable(TDb db, String tableName);

	public TTable getTable(TDb db, String tableName) {
		val res = findTable(db, tableName);
		if (null == res) {
			String dbName = db.getName();
			throw new CatalogRuntimeException("Table '" + dbName + "." + tableName + "' not found");
		}
		return res;
	}

	public void requireTableNotExists(TDb db, String tableName) {
		val res = findTable(db, tableName);
		if (null != res) {
			String dbName = db.getName();
			throw new CatalogRuntimeException("Table '" + dbName + "." + tableName + "' already exists");
		}
	}
	
	public List<TTable> getTablesByName(TDb db, List<String> tables) {
		return ListUtils.map(tables, n -> getTable(db, n));
	}

	public boolean tableExists(TDb db, String tableName) {
		val found = findTable(db, tableName);
		return null != found;
	}

	public abstract List<String> listTables(TDb db);

	public List<String> listTables(TDb db, String pattern) {
		val all = listTables(db);
		return SparkPatternUtils.filterPatternAndSort(all, pattern);
	}

	public boolean hasTable(TDb db) {
		return !listTables(db).isEmpty();
	}

	public abstract void addTable(TTable tbl);
	public abstract void removeTable(TTable tbl);

	public void removePutTable(TTable oldTable, TTable newTable) {
		removeTable(oldTable);
		addTable(newTable);
	}

}
