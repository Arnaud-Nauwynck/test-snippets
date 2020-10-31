package fr.an.metastore.spark;

import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;
import org.apache.spark.sql.catalyst.catalog.CatalogFunction;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;
import org.apache.spark.sql.catalyst.catalog.ExternalCatalog;

import scala.collection.Seq;

/**
 * java type for corresponding scala ExternalCatalog
 *
 */
public abstract class AbstractExternalCatalog implements ExternalCatalog {

	// --------------------------------------------------------------------------
	// Databases
	// --------------------------------------------------------------------------

	@Override
	public abstract void createDatabase(CatalogDatabase dbDefinition, boolean ignoreIfExists);

	@Override
	public abstract void dropDatabase(String db, boolean ignoreIfNotExists, boolean cascade);

	@Override
	public abstract void alterDatabase(CatalogDatabase dbDefinition);

	@Override
	public abstract CatalogDatabase getDatabase(String db);

	@Override
	public abstract boolean databaseExists(String db);

	@Override
	public abstract Seq<String> listDatabases();

	@Override
	public abstract Seq<String> listDatabases(String pattern);

	// --------------------------------------------------------------------------
	// Tables
	// --------------------------------------------------------------------------

	@Override
	public abstract void createTable(CatalogTable tableDefinition, boolean ignoreIfExists);

	@Override
	public abstract void dropTable(String db, String table, boolean ignoreIfNotExists, boolean purge);

	@Override
	public abstract void renameTable(String db, String oldName, String newName);

	@Override
	public abstract void alterTable(CatalogTable tableDefinition);

	@Override
	public abstract void alterTableDataSchema(String db, String table,
			org.apache.spark.sql.types.StructType newDataSchema);

	@Override
	public abstract void alterTableStats(String db, String table,
			scala.Option<org.apache.spark.sql.catalyst.catalog.CatalogStatistics> stats);

	@Override
	public abstract CatalogTable getTable(String db, String table);

	@Override
	public abstract Seq<CatalogTable> getTablesByName(String db, Seq<String> tables);

	@Override
	public abstract boolean tableExists(String db, String table);

	@Override
	public abstract Seq<String> listTables(String db);

	@Override
	public abstract Seq<String> listTables(String db, String pattern);

	@Override
	public abstract void loadTable(String db, String table, String loadPath, boolean isOverwrite, boolean isSrcLocal);

	// --------------------------------------------------------------------------
	// Partitions
	// --------------------------------------------------------------------------

	@Override
	public abstract void createPartitions(String db, String table, scala.collection.Seq<CatalogTablePartition> parts,
			boolean ignoreIfExists);

	@Override
	public abstract void dropPartitions(String db, String table,
			scala.collection.Seq<scala.collection.immutable.Map<String, String>> partSpecs, boolean ignoreIfNotExists,
			boolean purge, boolean retainData);

	@Override
	public abstract void renamePartitions(String db, String table,
			scala.collection.Seq<scala.collection.immutable.Map<String, String>> specs,
			scala.collection.Seq<scala.collection.immutable.Map<String, String>> newSpecs);

	@Override
	public abstract void alterPartitions(String db, String table, scala.collection.Seq<CatalogTablePartition> parts);

	@Override
	public abstract CatalogTablePartition getPartition(String db, String table,
			scala.collection.immutable.Map<String, String> spec);

	@Override
	public scala.Option<CatalogTablePartition> getPartitionOption(String db, String table,
			scala.collection.immutable.Map<String, String> spec) {
		CatalogTablePartition tmp = getPartition(db, table, spec);
		return (tmp != null) ? scala.Option.apply(tmp) : scala.Option.empty();
	}

	@Override
	public abstract Seq<String> listPartitionNames(String db, String table,
			scala.Option<scala.collection.immutable.Map<String, String>> spec);

	@Override
	public abstract Seq<CatalogTablePartition> listPartitions(String db, String table,
			scala.Option<scala.collection.immutable.Map<String, String>> spec);

	@Override
	public abstract Seq<CatalogTablePartition> listPartitionsByFilter(String db, String table,
			Seq<org.apache.spark.sql.catalyst.expressions.Expression> predicates, 
			String defaultTimeZoneId);

	@Override
	public abstract void loadPartition(String db, String table, String loadPath,
			scala.collection.immutable.Map<String, String> partition, boolean isOverwrite, boolean inheritTableSpecs,
			boolean isSrcLocal);

	@Override
	public abstract void loadDynamicPartitions(String db, String table, String loadPath,
			scala.collection.immutable.Map<String, String> partition, boolean replace, int numDP);


	// --------------------------------------------------------------------------
	// Functions
	// --------------------------------------------------------------------------

	@Override
	public abstract void createFunction(String db, CatalogFunction func);

	@Override
	public abstract void dropFunction(String db, String funcName);

	@Override
	public abstract void alterFunction(String db, CatalogFunction func);

	@Override
	public abstract void renameFunction(String db, String oldName, String newName);

	@Override
	public abstract CatalogFunction getFunction(String db, String funcName);

	@Override
	public abstract boolean functionExists(String db, String funcName);

	public abstract Seq<String> listFunctions(String db, String pattern);

}
