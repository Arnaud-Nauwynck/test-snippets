package fr.an.metastore.api;

import java.util.List;
import java.util.Optional;

import fr.an.metastore.api.dto.CatalogDatabaseDTO;
import fr.an.metastore.api.dto.CatalogFunctionDTO;
import fr.an.metastore.api.dto.CatalogTableDTO;
import fr.an.metastore.api.dto.CatalogTableDTO.CatalogStatisticsDTO;
import fr.an.metastore.api.dto.CatalogTablePartitionDTO;
import fr.an.metastore.api.dto.PartitionSpecDTO;
import fr.an.metastore.api.dto.StructTypeDTO;
import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;
import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;

/**
 * API similar to spark ExternalCatalog, 
 * but using only plain old java.util Collection/Map/List
 * and DTO or Immutable Value objects both for params and return types
 *
 */
public abstract class AbstractJavaDbCatalog {
	
	// --------------------------------------------------------------------------
	// Databases
	// --------------------------------------------------------------------------

	public abstract void setCurrentDatabase(String db);
	
	public abstract void createDatabase(String db, ImmutableCatalogDatabaseDef dbDefinition, boolean ignoreIfExists);

	public abstract void dropDatabase(String db, boolean ignoreIfNotExists, boolean cascade);

	public abstract void alterDatabase(String db, ImmutableCatalogDatabaseDef dbDefinition);

	public abstract CatalogDatabaseDTO getDatabase(String db);

	public abstract boolean databaseExists(String db);

	public abstract List<String> listDatabases();

	public abstract List<String> listDatabases(String pattern);

	// --------------------------------------------------------------------------
	// Tables
	// --------------------------------------------------------------------------

	public abstract void createTable(CatalogTableDTO tableDefinition, boolean ignoreIfExists);

	public abstract void dropTable(String db, String table, boolean ignoreIfNotExists, boolean purge);

	public abstract void renameTable(String db, String oldName, String newName);

	public abstract void alterTable(
			CatalogTableDTO tableDefinition);

	public abstract void alterTableDataSchema(String db, String table,
			StructTypeDTO newDataSchema);

	public abstract void alterTableStats(String db, String table,
			CatalogStatisticsDTO stats);

	public abstract CatalogTableDTO getTable(String db, String table);

	public abstract List<CatalogTableDTO> getTablesByName(String db, List<String> tables);

	public abstract boolean tableExists(String db, String table);

	public abstract List<String> listTables(String db);

	public abstract List<String> listTables(String db, String pattern);

	public abstract void loadTable(String db, String table, String loadPath, boolean isOverwrite, boolean isSrcLocal);

	// --------------------------------------------------------------------------
	// Partitions
	// --------------------------------------------------------------------------

	public abstract void createPartitions(String db, String table, 
			List<CatalogTablePartitionDTO> parts,
			boolean ignoreIfExists);

	public abstract void dropPartitions(String db, String table,
			List<ImmutablePartitionSpec> partSpecs, boolean ignoreIfNotExists,
			boolean purge, boolean retainData);

	public abstract void renamePartitions(String db, String table,
			List<ImmutablePartitionSpec> specs,
			List<ImmutablePartitionSpec> newSpecs);

	public abstract void alterPartitions(String db, String table, 
			List<CatalogTablePartitionDTO> parts);

	public abstract CatalogTablePartitionDTO getPartition(String db, String table,
			ImmutablePartitionSpec spec);

	public Optional<CatalogTablePartitionDTO> getPartitionOption(String db, String table,
			ImmutablePartitionSpec spec) {
		CatalogTablePartitionDTO tmp = getPartition(db, table, spec);
		return (tmp != null) ? Optional.of(tmp) : Optional.empty();
	}

	public abstract List<String> listPartitionNamesByPartialSpec(String db, String table,
			ImmutablePartitionSpec partialSpec);

	public abstract List<CatalogTablePartitionDTO> listPartitionsByPartialSpec(String db, String table,
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

	public abstract void createFunction(String db, String funcName, ImmutableCatalogFunctionDef funcDef);

	public abstract void dropFunction(String db, String funcName);

	public abstract void alterFunction(String db, String funcName, ImmutableCatalogFunctionDef funcDef);

	public abstract void renameFunction(String db, String oldFuncName, String newFuncName);

	public abstract CatalogFunctionDTO getFunction(String db, String funcName);

	public abstract boolean functionExists(String db, String funcName);

	public abstract List<String> listFunctions(String db, String pattern);

}
