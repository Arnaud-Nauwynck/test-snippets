package fr.an.metastore.api.spi;

import static fr.an.metastore.api.utils.MetastoreListUtils.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import fr.an.metastore.api.CatalogFacade;
import fr.an.metastore.api.dto.StructTypeDTO;
import fr.an.metastore.api.exceptions.CatalogRuntimeException;
import fr.an.metastore.api.exceptions.NoSuchTableRuntimeException;
import fr.an.metastore.api.exceptions.TableAlreadyExistsRuntimeException;
import fr.an.metastore.api.immutable.CatalogTableId;
import fr.an.metastore.api.immutable.CatalogFunctionId;
import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;
import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogTableStatistics;
import fr.an.metastore.api.immutable.ImmutableCatalogTablePartitionDef;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import fr.an.metastore.api.info.CatalogTableInfo;
import fr.an.metastore.api.info.CatalogTablePartitionInfo;
import fr.an.metastore.api.utils.MetastoreListUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * implementation of CatalogFacade, redispatching to Lookup/DDL managers 
 * for Databases / Tables / Partitions / Functions
 */
@RequiredArgsConstructor
public class DefaultDelegateCatalogFacade<
	TDb extends IDatabaseModel,
	TTable extends ITableModel,
	TTablePartition extends ITablePartitionModel,
	TFunction extends IFunctionModel
	> extends CatalogFacade {

	private final DatabasesLookup<TDb> dbsLookup;
	private final DatabasesDDL<TDb> dbsDdl;
	private final TablesLookup<TDb, TTable> dbTablesLookup;
	private final TablesDDL<TDb, TTable> dbTablesDdl;
	private final TablePartitionsLookup<TDb, TTable, TTablePartition> dbTablePartitionsLookup;
	private final TablePartitionsDDL<TDb, TTable, TTablePartition> dbTablePartitionsDdl;
	private final FunctionsLookup<TDb, TFunction> dbFuncsLookup;
	private final FunctionsDDL<TDb, TFunction> dbFuncsDdl;
	private final DataLoader<TDb,TTable,TTablePartition> dataLoaderManager;

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

	protected TDb geDatabaseModel(String dbName) {
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
		return db.getDbDef();
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

	@AllArgsConstructor
	private static class DbAndTable<TDb extends IDatabaseModel, TTable extends ITableModel> { 
		TDb db;
		TTable table;
	}
	
	@Override
	public void createTable(ImmutableCatalogTableDef tableDef, boolean ignoreIfExists) {
		val tableId = tableDef.getIdentifier();
		String dbName = tableId.database;
		val db = geDatabaseModel(dbName);
	    val tableName = tableId.table;
	    val found = dbTablesLookup.findTable(db, tableName);
	    if (null != found) {
	      if (!ignoreIfExists) {
	        throw new TableAlreadyExistsRuntimeException(tableId);
	      }
	    } else {
	    	val tbl = dbTablesDdl.createTable(db, tableDef, ignoreIfExists);
	    	dbTablesLookup.addTable(tbl);
	    }
	}

	@Override
	public void dropTable(CatalogTableId tableId, boolean ignoreIfNotExists, boolean purge) {
		val db = geDatabaseModel(tableId.database);
		val table = dbTablesLookup.findTable(db, tableId.table);
		if (null != table) {
			dbTablesDdl.dropTable(db, table, ignoreIfNotExists, purge);
			dbTablesLookup.removeTable(table);
		} else {
			if (!ignoreIfNotExists) {
				throw new NoSuchTableRuntimeException(tableId);
			}
		}
	}

	protected TTable getTable(TDb db, String tableName) {
		return dbTablesLookup.getTable(db, tableName);
	}

	protected DbAndTable<TDb,TTable> getDbAndTable(CatalogTableId tableId) {
		val db = geDatabaseModel(tableId.database);
		val table = dbTablesLookup.getTable(db, tableId.table);
		return new DbAndTable<>(db, table);
	}

	protected TTable doGetTable(CatalogTableId tableId) {
		val db = geDatabaseModel(tableId.database);
		return dbTablesLookup.getTable(db, tableId.table);
	}

	@Override
	public void renameTable(CatalogTableId oldTableId, String newTableName) {
		val old = getDbAndTable(oldTableId);
		dbTablesLookup.requireTableNotExists(old.db, newTableName);
		val newTable = dbTablesDdl.renameTable(old.db, old.table, newTableName);
		dbTablesLookup.removePutTable(old.table, newTable);
	}

	@Override
	public void alterTable(ImmutableCatalogTableDef tableDef) {
		val tableId = tableDef.getIdentifier();
		validate(tableId.database != null, "table database name not set");
		val t = getDbAndTable(tableId);
		dbTablesDdl.alterTable(t.db, t.table, tableDef);
	}

	@Override
	public void alterTableDataSchema(CatalogTableId tableId, 
			StructTypeDTO newDataSchema) {
		val t = getDbAndTable(tableId);
		dbTablesDdl.alterTableDataSchema(t.db, t.table, newDataSchema);
	}

	@Override
	public void alterTableStats(CatalogTableId tableId, ImmutableCatalogTableStatistics stats) {
		val t = getDbAndTable(tableId);
		dbTablesDdl.alterTableStats(t.db, t.table, stats);
	}

	@Override
	public ImmutableCatalogTableDef getTableDef(CatalogTableId tableId) {
		val t = getDbAndTable(tableId);
		return t.table.getDef();
	}

	@Override
	public CatalogTableInfo getTableInfo(CatalogTableId tableId) {
		val t = getDbAndTable(tableId);
		return toTableInfo(t.table);
	}

	protected CatalogTableInfo toTableInfo(TTable src) {
		return new CatalogTableInfo(src.getDef(), src.getLastAccessTime(), src.getStats());
	}

	@Override
	public List<ImmutableCatalogTableDef> getTableDefsByName(String dbName, List<String> tableNames) {
		val db = geDatabaseModel(dbName);
		return map(tableNames, n -> getTable(db, n).getDef());
	}

	@Override
	public List<CatalogTableInfo> getTableInfosByName(String dbName, List<String> tableNames) {
		val db = geDatabaseModel(dbName);
		return map(tableNames, n -> toTableInfo(getTable(db, n)));
	}

	@Override
	public boolean tableExists(CatalogTableId tableId) {
		val db = geDatabaseModel(tableId.database);
		return dbTablesLookup.tableExists(db, tableId.table);
	}

	@Override
	public List<String> listTableNames(String dbName) {
		val db = geDatabaseModel(dbName);
		return dbTablesLookup.listTables(db);
	}

	@Override
	public List<String> listTableNamesByPattern(String dbName, String pattern) {
		val db = geDatabaseModel(dbName);
		return dbTablesLookup.listTables(db, pattern);
	}

	@Override
	public void loadTable(CatalogTableId tableId, String loadPath, boolean isOverwrite, boolean isSrcLocal) {
		val t = getDbAndTable(tableId);
		dataLoaderManager.loadTable(t.db, t.table, loadPath, isOverwrite, isSrcLocal);
	}

	// --------------------------------------------------------------------------
	// Partitions
	// --------------------------------------------------------------------------

	@Override
	public void createPartitions(CatalogTableId tableId, 
			List<ImmutableCatalogTablePartitionDef> parts, boolean ignoreIfExists) {
		val t = getDbAndTable(tableId);
		val partModels = dbTablePartitionsDdl.createPartitions(t.db, t.table, parts, ignoreIfExists);
		dbTablePartitionsLookup.addPartitions(partModels);
	}

	@Override
	public void dropPartitions(CatalogTableId tableId, 
				List<ImmutablePartitionSpec> partSpecs, boolean ignoreIfNotExists,
				boolean purge, boolean retainData) {
		val t = getDbAndTable(tableId);
		val partModels = dbTablePartitionsLookup.getPartitions(t.db, t.table, partSpecs);
		dbTablePartitionsDdl.dropPartitions(t.db, t.table, partModels,
				ignoreIfNotExists, purge, retainData);
		dbTablePartitionsLookup.removePartitions(partModels);
	}

	@Override
	public void renamePartitions(CatalogTableId tableId, 
				List<ImmutablePartitionSpec> oldPartSpecs, 
				List<ImmutablePartitionSpec> newSpecs) {
		val t = getDbAndTable(tableId);
		validate(oldPartSpecs.size() == newSpecs.size(), "number of old and new partition specs differ");
		val oldPartModels = dbTablePartitionsLookup.getPartitions(t.db, t.table, oldPartSpecs);
		dbTablePartitionsLookup.requirePartitionsNotExist(t.db, t.table, newSpecs);
		List<TTablePartition> newPartModels = dbTablePartitionsDdl.renamePartitions(t.db, t.table, oldPartModels, newSpecs);
		dbTablePartitionsLookup.removeAddPartitions(oldPartModels, newPartModels);
	}

	@Override
	public void alterPartitions(CatalogTableId tableId, 
			List<ImmutableCatalogTablePartitionDef> partDefs) {
		val t = getDbAndTable(tableId);
		List<ImmutablePartitionSpec> partSpecs = map(partDefs, x -> x.getSpec());
		val partModels = dbTablePartitionsLookup.getPartitions(t.db, t.table, partSpecs);
		dbTablePartitionsDdl.alterPartitions(t.db, t.table, partModels, partDefs);
	}

	@Override
	public CatalogTablePartitionInfo getPartition(CatalogTableId tableId, ImmutablePartitionSpec spec) {
		val t = getDbAndTable(tableId);
		val tablePart = dbTablePartitionsLookup.getPartition(t.db, t.table, spec);
		return toTablePartitionInfo(tablePart);
	}

	protected CatalogTablePartitionInfo toTablePartitionInfo(TTablePartition src) {
		return new CatalogTablePartitionInfo(src.getDef(),
				src.getLastAccessTime(),
				src.getStats());
	}

	@Override
	public List<CatalogTablePartitionInfo> listPartitionsByPartialSpec(CatalogTableId tableId, 
			ImmutablePartitionSpec partialSpec) {
		val t = getDbAndTable(tableId);
		val tableParts = dbTablePartitionsLookup.listPartitionsByPartialSpec(t.db, t.table, partialSpec);
		return map(tableParts, x -> toTablePartitionInfo(x));
	}

	@Override
	public List<String> listPartitionNamesByPartialSpec(CatalogTableId tableId, 
			ImmutablePartitionSpec partialSpec) {
		val t = getDbAndTable(tableId);
		val tableParts = dbTablePartitionsLookup.listPartitionsByPartialSpec(t.db, t.table, partialSpec);
		return MetastoreListUtils.map(tableParts, x -> x.getPartitionName());
	}

//	@Override
//	public List<CatalogTablePartitionDTO> listPartitionsByFilter(CatalogTableId tableId, 
//			List<Expression> predicates,
//			String defaultTimeZoneId) {
//		val db = geDatabaseModel(dbName);
//		val table = getTable(db, tableName);
//		val tableParts = dbTablePartitionsLookup.listPartitionsByFilter(db, table, predicates, defaultTimeZoneId);
//		return dtoConverter.toTablePartitionDTOs(tableParts, table);
//	}

	@Override
	public void loadPartition(CatalogTableId tableId, String loadPath, 
			ImmutablePartitionSpec partSpec, boolean isOverwrite,
			boolean inheriTableModelSpecs, boolean isSrcLocal) {
		val t = getDbAndTable(tableId);
		val tablePart = dbTablePartitionsLookup.getPartition(t.db, t.table, partSpec);
		dataLoaderManager.loadPartition(t.db, t.table, tablePart, 
				loadPath, isOverwrite, inheriTableModelSpecs, isSrcLocal);
	}

	@Override
	public void loadDynamicPartitions(CatalogTableId tableId, String loadPath, 
			ImmutablePartitionSpec partSpec,
			boolean replace, int numDP) {
		val t = getDbAndTable(tableId);
		val tablePart = dbTablePartitionsLookup.getPartition(t.db, t.table, partSpec);
		dataLoaderManager.loadDynamicPartitions(t.db, t.table, tablePart,
				loadPath, replace, numDP);
	}

	// --------------------------------------------------------------------------
	// Functions
	// --------------------------------------------------------------------------

	@Override
	public void createFunction(ImmutableCatalogFunctionDef funcDef) {
		String dbName = funcDef.identifier.database;
		String funcName = funcDef.identifier.funcName;
		val db = geDatabaseModel(dbName);
		val found = dbFuncsLookup.findFunction(db, funcName);
		if (null == found) {
			throw new CatalogRuntimeException("Function already exists '" + dbName + "." + funcName + "'");
		}
		val func = dbFuncsDdl.createFunction(db, funcDef);
		dbFuncsLookup.add(func);
	}

	@Override
	public void dropFunction(CatalogFunctionId id) {
		val db = geDatabaseModel(id.database);
		val func = dbFuncsLookup.getFunction(db, id.funcName);
		dbFuncsDdl.dropFunction(db, func);
		dbFuncsLookup.remove(func);
	}

	@Override
	public void alterFunction(ImmutableCatalogFunctionDef funcDef) {
		val db = geDatabaseModel(funcDef.identifier.database);
		val func = dbFuncsLookup.getFunction(db, funcDef.identifier.funcName);
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
	public ImmutableCatalogFunctionDef getFunction(CatalogFunctionId id) {
		val db = geDatabaseModel(id.database);
		val func = dbFuncsLookup.getFunction(db, id.funcName);
		return func.getFuncDef();
	}

	@Override
	public boolean functionExists(CatalogFunctionId id) {
		val db = geDatabaseModel(id.database);
		return dbFuncsLookup.functionExists(db, id.funcName);
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
