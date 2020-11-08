package fr.an.metastore.api;

import java.util.List;
import java.util.Optional;

import fr.an.metastore.api.dto.StructTypeDTO;
import fr.an.metastore.api.immutable.CatalogFunctionId;
import fr.an.metastore.api.immutable.CatalogTableId;
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

	public abstract void dropTable(CatalogTableId tableId, boolean ignoreIfNotExists, boolean purge);

	public abstract void renameTable(CatalogTableId oldTableId, String newTableName);

	public abstract void alterTable(ImmutableCatalogTableDef tableDef);

	public abstract void alterTableDataSchema(CatalogTableId tableId,
			StructTypeDTO newDataSchema);

	public abstract void alterTableStats(CatalogTableId tableId,
			ImmutableCatalogTableStatistics stats);

	public abstract ImmutableCatalogTableDef getTableDef(CatalogTableId tableId);
	public abstract CatalogTableInfo getTableInfo(CatalogTableId tableId);

	public abstract List<ImmutableCatalogTableDef> getTableDefsByName(String db, List<String> tables);
	public abstract List<CatalogTableInfo> getTableInfosByName(String db, List<String> tables);

	public abstract boolean tableExists(CatalogTableId tableId);

	public abstract List<String> listTableNames(String db);

	public abstract List<String> listTableNamesByPattern(String db, String pattern);

	public abstract void loadTable(CatalogTableId tableId, String loadPath, boolean isOverwrite, boolean isSrcLocal);

	// --------------------------------------------------------------------------
	// Partitions
	// --------------------------------------------------------------------------

	public abstract void createPartitions(CatalogTableId tableId, 
			List<ImmutableCatalogTablePartitionDef> parts,
			boolean ignoreIfExists);

	public abstract void dropPartitions(CatalogTableId tableId,
			List<ImmutablePartitionSpec> partSpecs, boolean ignoreIfNotExists,
			boolean purge, boolean retainData);

	public abstract void renamePartitions(CatalogTableId tableId,
			List<ImmutablePartitionSpec> specs,
			List<ImmutablePartitionSpec> newSpecs);

	public abstract void alterPartitions(CatalogTableId tableId, 
			List<ImmutableCatalogTablePartitionDef> parts);

	public abstract CatalogTablePartitionInfo getPartition(CatalogTableId tableId,
			ImmutablePartitionSpec spec);

	public Optional<CatalogTablePartitionInfo> getPartitionOption(CatalogTableId tableId,
			ImmutablePartitionSpec spec) {
		CatalogTablePartitionInfo tmp = getPartition(tableId, spec);
		return (tmp != null) ? Optional.of(tmp) : Optional.empty();
	}

	public abstract List<String> listPartitionNamesByPartialSpec(CatalogTableId tableId,
			ImmutablePartitionSpec partialSpec);

	public abstract List<CatalogTablePartitionInfo> listPartitionsByPartialSpec(CatalogTableId tableId,
			ImmutablePartitionSpec partialSpec);

//	public abstract List<CatalogTablePartitionDTO> listPartitionsByFilter(CatalogTableId tableId,
//			List<ExpressionDTO> predicates, 
//			String defaultTimeZoneId);

	public abstract void loadPartition(CatalogTableId tableId, String loadPath,
			ImmutablePartitionSpec partition, 
			boolean isOverwrite, boolean inheritTableSpecs,
			boolean isSrcLocal);

	public abstract void loadDynamicPartitions(CatalogTableId tableId, String loadPath,
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
