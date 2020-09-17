package fr.an.dbcatalog.api.manager;

import org.apache.spark.sql.catalyst.catalog.CatalogStatistics;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.types.StructType;

import fr.an.dbcatalog.impl.model.DatabaseModel;
import fr.an.dbcatalog.impl.model.TableModel;

/**
 * part of AbstractJavaDbCatalog, for table DDL
 *
 */
public abstract class DatabaseTablesDDLManager<TDb extends DatabaseModel,TTable extends TableModel> {
	
	public abstract TTable createTable(TDb db, CatalogTable tableDefinition, boolean ignoreIfExists);

	public abstract void dropTable(TDb db, TTable table, boolean ignoreIfNotExists, boolean purge);

	public abstract TTable renameTable(TDb db, TTable table, //
			String newName);

	public abstract void alterTable(TDb db, TTable table, //
			CatalogTable tableDefinition);

	public abstract void alterTableDataSchema(TDb db, TTable table, //
			StructType newDataSchema);

	public abstract void alterTableStats(TDb db, TTable table, //
			CatalogStatistics stats);

}
