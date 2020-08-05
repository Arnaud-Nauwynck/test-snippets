package fr.an.spark.externalcatalog;


import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;
import org.apache.spark.sql.catalyst.catalog.CatalogFunction;
import org.apache.spark.sql.catalyst.catalog.CatalogStatistics;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;
import org.apache.spark.sql.catalyst.catalog.ExternalCatalog;
import org.apache.spark.sql.catalyst.expressions.Expression;
import org.apache.spark.sql.catalyst.util.StringUtils;
import org.apache.spark.sql.types.StructType;

import fr.an.spark.externalcatalog.impl.SparkCatalogHelper;
import scala.Option;
import scala.collection.Seq;
import scala.collection.immutable.Map;

public abstract class AbstractCachedExternalCatalog implements ExternalCatalog {

	private java.util.Map<String,CatalogDatabase> catalogDatabases;
	// private scala.collection.mutable.HashMap<String, DatabaseDesc> catalog = new scala.collection.mutable.HashMap<String, DatabaseDesc>();

	private String currentDatabase;
	
	// --------------------------------------------------------------------------------------------

	public AbstractCachedExternalCatalog() {
	}

	// --------------------------------------------------------------------------------------------

	public void setCurrentDatabase(String db) {
		this.currentDatabase = db;
	}

	// Database
	// --------------------------------------------------------------------------------------------

	@Override
	public Seq<String> listDatabases() {
		return SparkCatalogHelper.toScalaSeq(catalogDatabases.keySet());
	}

	@Override
	public Seq<String> listDatabases(String pattern) {
		return StringUtils.filterPattern(listDatabases(), pattern);
	}

	@Override
	public boolean databaseExists(String db) {
		return catalogDatabases.containsKey(db);
	}

	@Override
	public CatalogDatabase getDatabase(String db) {
		return catalogDatabases.get(db);
	}

	
	// Tables
	// --------------------------------------------------------------------------------------------


	@Override
	public Seq<String> listTables(String db, String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Seq<String> listTables(String db) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean tableExists(String db, String table) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CatalogTable getTable(String db, String table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Seq<CatalogTable> getTablesByName(String db, Seq<String> tables) {
		// TODO Auto-generated method stub
		return null;
	}

	
	// Partitions
	// --------------------------------------------------------------------------------------------

	@Override
	public CatalogTablePartition getPartition(String db, String table, Map<String, String> spec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Option<CatalogTablePartition> getPartitionOption(String db, String table, Map<String, String> spec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Seq<String> listPartitionNames(String db, String table, Option<Map<String, String>> partialSpec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Seq<CatalogTablePartition> listPartitions(String db, String table, Option<Map<String, String>> partialSpec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Seq<CatalogTablePartition> listPartitionsByFilter(String db, String table, Seq<Expression> predicates,
			String defaultTimeZoneId) {
		// TODO Auto-generated method stub
		return null;
	}
	


	// Functions
	// --------------------------------------------------------------------------------------------

	@Override
	public Seq<String> listFunctions(String db, String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean functionExists(String db, String funcName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CatalogFunction getFunction(String db, String funcName) {
		// TODO Auto-generated method stub
		return null;
	}

	// Load
	// --------------------------------------------------------------------------------------------


	@Override
	public void loadTable(String db, String table, String loadPath, boolean isOverwrite, boolean isSrcLocal) {
		// TODO Auto-generated method stub
		
	}

	public void loadDynamicPartitions(String db, String table, String loadPath, Map<String, String> partition,
			boolean replace, int numDP) {
		// TODO Auto-generated method stub
		
	}

	public void loadPartition(String db, String table, String loadPath, Map<String, String> partition,
			boolean isOverwrite, boolean inheritTableSpecs, boolean isSrcLocal) {
		// TODO Auto-generated method stub
		
	}
	
	// DDL Modification of Metastore
	// --------------------------------------------------------------------------------------------
	
	public void alterDatabase(CatalogDatabase dbDefinition) {
		// TODO Auto-generated method stub
		
	}

	public void alterFunction(String db, CatalogFunction funcDefinition) {
		// TODO Auto-generated method stub
		
	}

	public void alterPartitions(String db, String table, Seq<CatalogTablePartition> parts) {
		// TODO Auto-generated method stub
		
	}

	public void alterTable(CatalogTable tableDefinition) {
		// TODO Auto-generated method stub
		
	}

	public void alterTableDataSchema(String db, String table, StructType newDataSchema) {
		// TODO Auto-generated method stub
		
	}

	public void alterTableStats(String db, String table, Option<CatalogStatistics> stats) {
		// TODO Auto-generated method stub
		
	}

	public void createDatabase(CatalogDatabase dbDefinition, boolean ignoreIfExists) {
		// TODO Auto-generated method stub
		
	}

	public void createFunction(String db, CatalogFunction funcDefinition) {
		// TODO Auto-generated method stub
		
	}

	public void createPartitions(String db, String table, Seq<CatalogTablePartition> parts, boolean ignoreIfExists) {
		// TODO Auto-generated method stub
		
	}

	public void createTable(CatalogTable tableDefinition, boolean ignoreIfExists) {
		// TODO Auto-generated method stub
		
	}

	public void dropDatabase(String db, boolean ignoreIfNotExists, boolean cascade) {
		// TODO Auto-generated method stub
		
	}

	public void dropFunction(String db, String funcName) {
		// TODO Auto-generated method stub
		
	}

	public void dropPartitions(String db, String table, Seq<Map<String, String>> parts, boolean ignoreIfNotExists,
			boolean purge, boolean retainData) {
		// TODO Auto-generated method stub
		
	}

	public void dropTable(String db, String table, boolean ignoreIfNotExists, boolean purge) {
		// TODO Auto-generated method stub
		
	}

	public void renameFunction(String db, String oldName, String newName) {
		// TODO Auto-generated method stub
		
	}

	public void renamePartitions(String db, String table, Seq<Map<String, String>> specs,
			Seq<Map<String, String>> newSpecs) {
		// TODO Auto-generated method stub
		
	}

	public void renameTable(String db, String oldName, String newName) {
		// TODO Auto-generated method stub
		
	}

	
}
