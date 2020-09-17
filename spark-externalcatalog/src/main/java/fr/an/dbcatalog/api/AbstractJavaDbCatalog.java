package fr.an.dbcatalog.api;

import java.util.List;

import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;
import org.apache.spark.sql.catalyst.catalog.CatalogFunction;
import org.apache.spark.sql.catalyst.catalog.CatalogStatistics;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;
import org.apache.spark.sql.catalyst.expressions.Expression;
import org.apache.spark.sql.types.StructType;

/**
 * API similar to spark ExternalCatalog, but using only plain old java.util Collection/Map/List
 *
 */
public abstract class AbstractJavaDbCatalog {
	
	// --------------------------------------------------------------------------
	// Databases
	// --------------------------------------------------------------------------

	public abstract void setCurrentDatabase(String db);
	
	public abstract void createDatabase(CatalogDatabase dbDefinition, boolean ignoreIfExists);

	public abstract void dropDatabase(String db, boolean ignoreIfNotExists, boolean cascade);

	public abstract void alterDatabase(CatalogDatabase dbDefinition);

	public abstract CatalogDatabase getDatabase(String db);

	public abstract boolean databaseExists(String db);

	public abstract List<String> listDatabases();

	public abstract List<String> listDatabases(String pattern);

	// --------------------------------------------------------------------------
	// Tables
	// --------------------------------------------------------------------------

	public abstract void createTable(CatalogTable tableDefinition, boolean ignoreIfExists);

	public abstract void dropTable(String db, String table, boolean ignoreIfNotExists, boolean purge);

	public abstract void renameTable(String db, String oldName, String newName);

	public abstract void alterTable(CatalogTable tableDefinition);

	public abstract void alterTableDataSchema(String db, String table,
			StructType newDataSchema);

	public abstract void alterTableStats(String db, String table,
			CatalogStatistics stats);

	public abstract CatalogTable getTable(String db, String table);

	public abstract List<CatalogTable> getTablesByName(String db, List<String> tables);

	public abstract boolean tableExists(String db, String table);

	public abstract List<String> listTables(String db);

	public abstract List<String> listTables(String db, String pattern);

	public abstract void loadTable(String db, String table, String loadPath, boolean isOverwrite, boolean isSrcLocal);

	// --------------------------------------------------------------------------
	// Partitions
	// --------------------------------------------------------------------------

	public abstract void createPartitions(String db, String table, List<CatalogTablePartition> parts,
			boolean ignoreIfExists);

	public abstract void dropPartitions(String db, String table,
			List<PartitionSpec> partSpecs, boolean ignoreIfNotExists,
			boolean purge, boolean retainData);

	public abstract void renamePartitions(String db, String table,
			List<PartitionSpec> specs,
			List<PartitionSpec> newSpecs);

	public abstract void alterPartitions(String db, String table, List<CatalogTablePartition> parts);

	public abstract CatalogTablePartition getPartition(String db, String table,
			PartitionSpec spec);

	public scala.Option<CatalogTablePartition> getPartitionOption(String db, String table,
			PartitionSpec spec) {
		CatalogTablePartition tmp = getPartition(db, table, spec);
		return (tmp != null) ? scala.Option.apply(tmp) : scala.Option.empty();
	}

	public abstract List<String> listPartitionNames(String db, String table,
			PartitionSpec spec);

	public abstract List<CatalogTablePartition> listPartitions(String db, String table,
			PartitionSpec spec);

	public abstract List<CatalogTablePartition> listPartitionsByFilter(String db, String table,
			List<Expression> predicates, 
			String defaultTimeZoneId);

	public abstract void loadPartition(String db, String table, String loadPath,
			PartitionSpec partition, boolean isOverwrite, boolean inheritTableSpecs,
			boolean isSrcLocal);

	public abstract void loadDynamicPartitions(String db, String table, String loadPath,
			PartitionSpec partition, boolean replace, int numDP);


	// --------------------------------------------------------------------------
	// Functions
	// --------------------------------------------------------------------------

	public abstract void createFunction(String db, CatalogFunction func);

	public abstract void dropFunction(String db, String funcName);

	public abstract void alterFunction(String db, CatalogFunction func);

	public abstract void renameFunction(String db, String oldName, String newName);

	public abstract CatalogFunction getFunction(String db, String funcName);

	public abstract boolean functionExists(String db, String funcName);

	public abstract List<String> listFunctions(String db, String pattern);

}
