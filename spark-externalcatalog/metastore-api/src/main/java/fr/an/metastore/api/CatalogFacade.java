package fr.an.metastore.api;

import java.util.List;
import java.util.Optional;

import fr.an.metastore.api.dto.StructTypeDTO;
import fr.an.metastore.api.immutable.CatalogFunctionId;
import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;
import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogTableStatistics;
import fr.an.metastore.api.immutable.ImmutableCatalogTablePartitionDef;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import fr.an.metastore.api.info.CatalogTableInfo;
import fr.an.metastore.api.info.CatalogTablePartitionInfo;

/**
 * API similar to spark ExternalCatalog, 
 * but using only plain old java.util Collection/Map/List
 * and DTO or Immutable Value objects both for params and return types
 *
 */
public abstract class CatalogFacade {
	
	// --------------------------------------------------------------------------
	// Databases
	// --------------------------------------------------------------------------

	public abstract void setCurrentDatabase(String db);
	
	public abstract void createDatabase(String db, ImmutableCatalogDatabaseDef dbDefinition, boolean ignoreIfExists);

	public abstract void dropDatabase(String db, boolean ignoreIfNotExists, boolean cascade);

	public abstract void alterDatabase(String db, ImmutableCatalogDatabaseDef dbDefinition);

	public abstract ImmutableCatalogDatabaseDef getDatabase(String db);

	public abstract boolean databaseExists(String db);

	public abstract List<String> listDatabases();

	public abstract List<String> listDatabases(String pattern);

	// --------------------------------------------------------------------------
	// Tables
	// --------------------------------------------------------------------------

	public abstract void createTable(ImmutableCatalogTableDef tableDef, boolean ignoreIfExists);

	public abstract void dropTable(String db, String table, boolean ignoreIfNotExists, boolean purge);

	public abstract void renameTable(String db, String oldTableName, String newTableName);

	public abstract void alterTable(ImmutableCatalogTableDef tableDef);

	public abstract void alterTableDataSchema(String db, String table,
			StructTypeDTO newDataSchema);

	public abstract void alterTableStats(String db, String table,
			ImmutableCatalogTableStatistics stats);

	public abstract ImmutableCatalogTableDef getTableDef(String db, String table);
	public abstract CatalogTableInfo getTableInfo(String db, String table);

	public abstract List<ImmutableCatalogTableDef> getTableDefsByName(String db, List<String> tables);
	public abstract List<CatalogTableInfo> getTableInfosByName(String db, List<String> tables);

	public abstract boolean tableExists(String db, String table);

	public abstract List<String> listTableNames(String db);

	public abstract List<String> listTableNamesByPattern(String db, String pattern);

	public abstract void loadTable(String db, String table, String loadPath, boolean isOverwrite, boolean isSrcLocal);

	// --------------------------------------------------------------------------
	// Partitions
	// --------------------------------------------------------------------------

	public abstract void createPartitions(String db, String table, 
			List<ImmutableCatalogTablePartitionDef> parts,
			boolean ignoreIfExists);

	public abstract void dropPartitions(String db, String table,
			List<ImmutablePartitionSpec> partSpecs, boolean ignoreIfNotExists,
			boolean purge, boolean retainData);

	public abstract void renamePartitions(String db, String table,
			List<ImmutablePartitionSpec> specs,
			List<ImmutablePartitionSpec> newSpecs);

	public abstract void alterPartitions(String db, String table, 
			List<ImmutableCatalogTablePartitionDef> parts);

	public abstract CatalogTablePartitionInfo getPartition(String db, String table,
			ImmutablePartitionSpec spec);

	public Optional<CatalogTablePartitionInfo> getPartitionOption(String db, String table,
			ImmutablePartitionSpec spec) {
		CatalogTablePartitionInfo tmp = getPartition(db, table, spec);
		return (tmp != null) ? Optional.of(tmp) : Optional.empty();
	}

	public abstract List<String> listPartitionNamesByPartialSpec(String db, String table,
			ImmutablePartitionSpec partialSpec);

	public abstract List<CatalogTablePartitionInfo> listPartitionsByPartialSpec(String db, String table,
			ImmutablePartitionSpec partialSpec);

//	public abstract List<CatalogTablePartitionDTO> listPartitionsByFilter(String db, String table,
//			List<ExpressionDTO> predicates, 
//			String defaultTimeZoneId);

	public abstract void loadPartition(String db, String table, String loadPath,
			ImmutablePartitionSpec partition, 
			boolean isOverwrite, boolean inheritTableSpecs,
			boolean isSrcLocal);

	public abstract void loadDynamicPartitions(String db, String table, String loadPath,
			ImmutablePartitionSpec partition, 
			boolean replace, int numDP);


	// --------------------------------------------------------------------------
	// Functions
	// --------------------------------------------------------------------------

	public abstract void createFunction(ImmutableCatalogFunctionDef funcDef);

	public abstract void dropFunction(CatalogFunctionId identifier);

	public abstract void alterFunction(ImmutableCatalogFunctionDef funcDef);

	public abstract void renameFunction(String db, String oldFuncName, String newFuncName);

	public abstract ImmutableCatalogFunctionDef getFunction(CatalogFunctionId identifier);

	public abstract boolean functionExists(CatalogFunctionId identifier);

	public abstract List<String> listFunctions(String db, String pattern);

}
