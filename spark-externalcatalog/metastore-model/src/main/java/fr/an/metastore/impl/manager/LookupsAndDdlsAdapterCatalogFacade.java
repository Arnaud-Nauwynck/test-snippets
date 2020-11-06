package fr.an.metastore.impl.manager;

import static fr.an.metastore.impl.utils.MetastoreListUtils.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import fr.an.metastore.api.CatalogFacade;
import fr.an.metastore.api.dto.CatalogFunctionDTO;
import fr.an.metastore.api.dto.CatalogTableDTO;
import fr.an.metastore.api.dto.CatalogTableDTO.CatalogStatisticsDTO;
import fr.an.metastore.api.dto.StructTypeDTO;
import fr.an.metastore.api.exceptions.CatalogRuntimeException;
import fr.an.metastore.api.exceptions.NoSuchTableRuntimeException;
import fr.an.metastore.api.exceptions.TableAlreadyExistsRuntimeException;
import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;
import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTablePartitionDef;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import fr.an.metastore.api.info.CatalogTablePartitionInfo;
import fr.an.metastore.api.manager.DataLoaderManager;
import fr.an.metastore.api.manager.DatabasesDDLManager;
import fr.an.metastore.api.manager.DatabasesLookup;
import fr.an.metastore.api.manager.FunctionsDDLManager;
import fr.an.metastore.api.manager.FunctionsLookup;
import fr.an.metastore.api.manager.TablePartitionsDDLManager;
import fr.an.metastore.api.manager.TablePartitionsLookup;
import fr.an.metastore.api.manager.TablesDDLManager;
import fr.an.metastore.api.manager.TablesLookup;
import fr.an.metastore.impl.model.CatalogModel2DtoConverter;
import fr.an.metastore.impl.model.DatabaseModel;
import fr.an.metastore.impl.model.FunctionModel;
import fr.an.metastore.impl.model.TableModel;
import fr.an.metastore.impl.model.TablePartitionModel;
import fr.an.metastore.impl.utils.MetastoreListUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * implementation of CatalogFacade, redispatching to Lookup/DDL managers 
 * for Databases / Tables / Partitions / Functions
 */
@RequiredArgsConstructor
public class LookupsAndDdlsAdapterCatalogFacade extends CatalogFacade {

	private final CatalogModel2DtoConverter dtoConverter;
	private final DatabasesLookup<DatabaseModel> dbsLookup;
	private final DatabasesDDLManager<DatabaseModel> dbsDdl;
	private final TablesLookup<DatabaseModel, TableModel> dbTablesLookup;
	private final TablesDDLManager<DatabaseModel, TableModel> dbTablesDdl;
	private final TablePartitionsLookup<DatabaseModel, TableModel, TablePartitionModel> dbTablePartitionsLookup;
	private final TablePartitionsDDLManager<DatabaseModel, TableModel, TablePartitionModel> dbTablePartitionsDdl;
	private final FunctionsLookup<DatabaseModel, FunctionModel> dbFuncsLookup;
	private final FunctionsDDLManager<DatabaseModel, FunctionModel> dbFuncsDdl;
	private final DataLoaderManager<DatabaseModel,TableModel,TablePartitionModel> dataLoaderManager;

	String currentDatabase = "default";

	// --------------------------------------------------------------------------
	// Databases
	// --------------------------------------------------------------------------

	@Override
	public void setCurrentDatabase(String db) {
		this.currentDatabase = db;
	}

	@Override
	public synchronized void createDatabase(String dbName, ImmutableCatalogDatabaseDef dbDef, boolean ignoreIfExists) {
		val found = dbsLookup.findDatabase(dbName);
		if (null != found) {
	        if (!ignoreIfExists) {
	        	throw new CatalogRuntimeException("Database already exists '" + dbName + "'");
	        }
		} else {
			val db = dbsDdl.createDatabase(dbName, dbDef, ignoreIfExists);
			dbsLookup.addDatabase(db);
		}
	}

	@Override
	public void dropDatabase(String dbName, boolean ignoreIfNotExists, boolean cascade) {
		val db = dbsLookup.findDatabase(dbName);
		if (null != db) {
			if (!cascade) {
				// If cascade is false, make sure the database is empty.
				if (dbTablesLookup.hasTable(db)) {
					throw new CatalogRuntimeException("Database '" + dbName + "' is not empty. One or more tables exist.");
				}
				if (dbFuncsLookup.hasFunction(db)) {
					throw new CatalogRuntimeException("Database '" + dbName + "' is not empty. One or more functions exist.");
				}
			}
			dbsDdl.dropDatabase(db, ignoreIfNotExists, cascade);
			dbsLookup.removeDatabase(db);
		} else {
			if (!ignoreIfNotExists) {
				throw new CatalogRuntimeException("No such database '" + dbName + "'");
			}
		}
	}

	protected DatabaseModel geDatabaseModel(String dbName) {
		return dbsLookup.getDatabase(dbName);
	}

	@Override
	public void alterDatabase(String dbName, ImmutableCatalogDatabaseDef dbDef) {
		val db = geDatabaseModel(dbName);
		dbsDdl.alterDatabase(db, dbDef);
	}

	@Override
	public ImmutableCatalogDatabaseDef getDatabase(String dbName) {
		val db = geDatabaseModel(dbName);
		return db.getDbDef(); // dtoConverter.toDbDTO(db);
	}

	@Override
	public boolean databaseExists(String db) {
		return dbsLookup.databaseExists(db);
	}

	@Override
	public List<String> listDatabases() {
		val tmpres = dbsLookup.listDatabases();
		return toSortedList(tmpres);
	}

	@Override
	public List<String> listDatabases(String pattern) {
		val tmpres = dbsLookup.listDatabases(pattern);
		return toSortedList(tmpres);
	}

	// --------------------------------------------------------------------------
	// Tables
	// --------------------------------------------------------------------------

	@Override
	public void createTable(ImmutableCatalogTableDef tableDef, boolean ignoreIfExists) {
		val tableId = tableDef.getIdentifier();
		String dbName = tableId.database;
		val db = geDatabaseModel(dbName);
	    val tableName = tableId.table;
	    val found = dbTablesLookup.findTable(db, tableName);
	    if (null != found) {
	      if (!ignoreIfExists) {
	        throw new TableAlreadyExistsRuntimeException(dbName, tableName);
	      }
	    } else {
	    	val tbl = dbTablesDdl.createTable(db, tableDef, ignoreIfExists);
	    	dbTablesLookup.addTable(tbl);
	    }
	}

	@Override
	public void dropTable(String dbName, String tableName, boolean ignoreIfNotExists, boolean purge) {
		val db = geDatabaseModel(dbName);
		val table = dbTablesLookup.findTable(db, tableName);
		if (null != table) {
			dbTablesDdl.dropTable(db, table, ignoreIfNotExists, purge);
			dbTablesLookup.removeTable(table);
		} else {
			if (!ignoreIfNotExists) {
				throw new NoSuchTableRuntimeException(dbName, tableName);
			}
		}
	}

	protected TableModel getTable(DatabaseModel db, String tableName) {
		return dbTablesLookup.getTable(db, tableName);
	}

	protected TableModel dogetTable(String dbName, String tableName) {
		val db = geDatabaseModel(dbName);
		return dbTablesLookup.getTable(db, tableName);
	}

	@Override
	public void renameTable(String dbName, String oldTableName, String newTableName) {
		val db = geDatabaseModel(dbName);
		val table = dbTablesLookup.getTable(db, oldTableName);
		dbTablesLookup.requireTableNotExists(db, newTableName);
		val newTable = dbTablesDdl.renameTable(db, table, newTableName);
		dbTablesLookup.removePutTable(table, newTable);
	}

	@Override
	public void alterTable(ImmutableCatalogTableDef tableDef) {
		val tableId = tableDef.getIdentifier();
		String dbName = tableId.database;
		validate(dbName != null, "table database name not set");
		val db = geDatabaseModel(dbName);
		val table = getTable(db, tableId.table);
		dbTablesDdl.alterTable(db, table, tableDef);
	}

	@Override
	public void alterTableDataSchema(String dbName, String tableName, 
			StructTypeDTO newDataSchema) {
		val db = geDatabaseModel(dbName);
		val table = getTable(db, tableName);
		dbTablesDdl.alterTableDataSchema(db, table, newDataSchema);
	}

	@Override
	public void alterTableStats(String dbName, String tableName, CatalogStatisticsDTO stats) {
		val db = geDatabaseModel(dbName);
		val table = getTable(db, tableName);
		dbTablesDdl.alterTableStats(db, table, stats);
	}

	@Override
	public ImmutableCatalogTableDef getTableDef(String db, String table) {
		val t = dogetTable(db, table);
		return t.getDef();
	}

	@Override
	public CatalogTableDTO getTable(String db, String table) {
		val t = dogetTable(db, table);
		return dtoConverter.toTableDTO(t);
	}

	@Override
	public List<ImmutableCatalogTableDef> getTableDefsByName(String dbName, List<String> tableNames) {
		val db = geDatabaseModel(dbName);
		return MetastoreListUtils.map(tableNames, n -> getTable(db, n).getDef());
	}

	@Override
	public List<CatalogTableDTO> getTablesByName(String dbName, List<String> tableNames) {
		val db = geDatabaseModel(dbName);
		return MetastoreListUtils.map(tableNames, n -> dtoConverter.toTableDTO(getTable(db, n)));
	}

	@Override
	public boolean tableExists(String dbName, String tableName) {
		val db = geDatabaseModel(dbName);
		return dbTablesLookup.tableExists(db, tableName);
	}

	@Override
	public List<String> listTables(String dbName) {
		val db = geDatabaseModel(dbName);
		return dbTablesLookup.listTables(db);
	}

	@Override
	public List<String> listTables(String dbName, String pattern) {
		val db = geDatabaseModel(dbName);
		return dbTablesLookup.listTables(db, pattern);
	}

	@Override
	public void loadTable(String dbName, String tableName, String loadPath, boolean isOverwrite, boolean isSrcLocal) {
		val db = geDatabaseModel(dbName);
		val table = getTable(db, tableName);
		dataLoaderManager.loadTable(db, table, loadPath, isOverwrite, isSrcLocal);
	}

	// --------------------------------------------------------------------------
	// Partitions
	// --------------------------------------------------------------------------

	@Override
	public void createPartitions(String dbName, String tableName, 
			List<ImmutableCatalogTablePartitionDef> parts, boolean ignoreIfExists) {
		val db = geDatabaseModel(dbName);
		val table = getTable(db, tableName);
		val partModels = dbTablePartitionsDdl.createPartitions(db, table, parts, ignoreIfExists);
		dbTablePartitionsLookup.addPartitions(partModels);
	}

	@Override
	public void dropPartitions(String dbName, String tableName, 
				List<ImmutablePartitionSpec> partSpecs, boolean ignoreIfNotExists,
				boolean purge, boolean retainData) {
		val db = geDatabaseModel(dbName);
		val table = getTable(db, tableName);
		val partModels = dbTablePartitionsLookup.getPartitions(db, table, partSpecs);
		dbTablePartitionsDdl.dropPartitions(db, table, partModels,
				ignoreIfNotExists, purge, retainData);
		dbTablePartitionsLookup.removePartitions(partModels);
	}

	@Override
	public void renamePartitions(String dbName, String tableName, 
				List<ImmutablePartitionSpec> oldPartSpecs, 
				List<ImmutablePartitionSpec> newSpecs) {
		val db = geDatabaseModel(dbName);
		val table = getTable(db, tableName);
		validate(oldPartSpecs.size() == newSpecs.size(), "number of old and new partition specs differ");
		val oldPartModels = dbTablePartitionsLookup.getPartitions(db, table, oldPartSpecs);
		dbTablePartitionsLookup.requirePartitionsNotExist(db, table, newSpecs);
		List<TablePartitionModel> newPartModels = dbTablePartitionsDdl.renamePartitions(db, table, oldPartModels, newSpecs);
		dbTablePartitionsLookup.removeAddPartitions(oldPartModels, newPartModels);
	}

	@Override
	public void alterPartitions(String dbName, String tableName, 
			List<ImmutableCatalogTablePartitionDef> partDefs) {
		val db = geDatabaseModel(dbName);
		val table = getTable(db, tableName);
		List<ImmutablePartitionSpec> partSpecs = map(partDefs, x -> x.getSpec());
		val partModels = dbTablePartitionsLookup.getPartitions(db, table, partSpecs);
		dbTablePartitionsDdl.alterPartitions(db, table, partModels, partDefs);
	}

	@Override
	public CatalogTablePartitionInfo getPartition(String dbName, String tableName, ImmutablePartitionSpec spec) {
		val db = geDatabaseModel(dbName);
		val table = getTable(db, tableName);
		val tablePart = dbTablePartitionsLookup.getPartition(db, table, spec);
		return dtoConverter.toTablePartitionInfo(tablePart, table);
	}

	@Override
	public List<CatalogTablePartitionInfo> listPartitionsByPartialSpec(String dbName, String tableName, 
			ImmutablePartitionSpec partialSpec) {
		val db = geDatabaseModel(dbName);
		val table = getTable(db, tableName);
		val tableParts = dbTablePartitionsLookup.listPartitionsByPartialSpec(db, table, partialSpec);
		return dtoConverter.toTablePartitionInfos(tableParts, table);
	}

	@Override
	public List<String> listPartitionNamesByPartialSpec(String dbName, String tableName, 
			ImmutablePartitionSpec partialSpec) {
		val db = geDatabaseModel(dbName);
		val table = getTable(db, tableName);
		val tableParts = dbTablePartitionsLookup.listPartitionsByPartialSpec(db, table, partialSpec);
		return MetastoreListUtils.map(tableParts, x -> x.getPartitionName());
	}

//	@Override
//	public List<CatalogTablePartitionDTO> listPartitionsByFilter(String dbName, String tableName, 
//			List<Expression> predicates,
//			String defaultTimeZoneId) {
//		val db = geDatabaseModel(dbName);
//		val table = getTable(db, tableName);
//		val tableParts = dbTablePartitionsLookup.listPartitionsByFilter(db, table, predicates, defaultTimeZoneId);
//		return dtoConverter.toTablePartitionDTOs(tableParts, table);
//	}

	@Override
	public void loadPartition(String dbName, String tableName, String loadPath, 
			ImmutablePartitionSpec partSpec, boolean isOverwrite,
			boolean inheriTableModelSpecs, boolean isSrcLocal) {
		val db = geDatabaseModel(dbName);
		val table = getTable(db, tableName);
		val tablePart = dbTablePartitionsLookup.getPartition(db, table, partSpec);
		dataLoaderManager.loadPartition(db, table, tablePart, 
				loadPath, isOverwrite, inheriTableModelSpecs, isSrcLocal);
	}

	@Override
	public void loadDynamicPartitions(String dbName, String tableName, String loadPath, 
			ImmutablePartitionSpec partSpec,
			boolean replace, int numDP) {
		val db = geDatabaseModel(dbName);
		val table = getTable(db, tableName);
		val tablePart = dbTablePartitionsLookup.getPartition(db, table, partSpec);
		dataLoaderManager.loadDynamicPartitions(db, table, tablePart,
				loadPath, replace, numDP);
	}

	// --------------------------------------------------------------------------
	// Functions
	// --------------------------------------------------------------------------

	@Override
	public void createFunction(String dbName, String funcName, ImmutableCatalogFunctionDef funcDef) {
		val db = geDatabaseModel(dbName);
		val found = dbFuncsLookup.findFunction(db, funcName);
		if (null == found) {
			throw new CatalogRuntimeException("Function already exists '" + dbName + "." + funcName + "'");
		}
		val func = dbFuncsDdl.createFunction(db, funcName, funcDef);
		dbFuncsLookup.add(func);
	}

	@Override
	public void dropFunction(String dbName, String funcName) {
		val db = geDatabaseModel(dbName);
		val func = dbFuncsLookup.getFunction(db, funcName);
		dbFuncsDdl.dropFunction(db, func);
		dbFuncsLookup.remove(func);
	}

	@Override
	public void alterFunction(String dbName, String funcName, ImmutableCatalogFunctionDef funcDef) {
		val db = geDatabaseModel(dbName);
		val func = dbFuncsLookup.getFunction(db, funcName);
		dbFuncsDdl.alterFunction(db, func, funcDef);
	}

	@Override
	public void renameFunction(String dbName, String oldFuncName, String newFuncName) {
		val db = geDatabaseModel(dbName);
		val oldFunc = dbFuncsLookup.getFunction(db, oldFuncName);
		dbFuncsLookup.requireFunctionNotExists(db, newFuncName);
		val newFunc = dbFuncsDdl.renameFunction(db, oldFunc, newFuncName);
		dbFuncsLookup.removeAdd(oldFunc, newFunc);
	}
	
	@Override
	public CatalogFunctionDTO getFunction(String dbName, String funcName) {
		val db = geDatabaseModel(dbName);
		val func = dbFuncsLookup.getFunction(db, funcName);
		return dtoConverter.toFunctionDTO(func);
	}

	@Override
	public boolean functionExists(String dbName, String funcName) {
		val db = geDatabaseModel(dbName);
		return dbFuncsLookup.functionExists(db, funcName);
	}

	@Override
	public List<String> listFunctions(String dbName, String pattern) {
		val db = geDatabaseModel(dbName);
		return dbFuncsLookup.listFunctions(db, pattern);
	}

	// --------------------------------------------------------------------------------------------

	private static List<String> toSortedList(Collection<String> src) {
		return new ArrayList<>(new TreeSet<>(src));
	}

	private static void validate(boolean expectTrue, String message) {
		if (!expectTrue) {
			throw new CatalogRuntimeException(message);
		}
	}

}
