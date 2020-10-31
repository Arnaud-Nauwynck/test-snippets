package fr.an.metastore.impl.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import fr.an.metastore.api.AbstractJavaDbCatalog;
import fr.an.metastore.api.dto.CatalogDatabaseDTO;
import fr.an.metastore.api.dto.CatalogFunctionDTO;
import fr.an.metastore.api.dto.CatalogTableDTO;
import fr.an.metastore.api.dto.CatalogTableDTO.CatalogStatisticsDTO;
import fr.an.metastore.api.dto.CatalogTablePartitionDTO;
import fr.an.metastore.api.dto.StructTypeDTO;
import fr.an.metastore.api.exceptions.CatalogRuntimeException;
import fr.an.metastore.api.exceptions.NoSuchTableRuntimeException;
import fr.an.metastore.api.exceptions.TableAlreadyExistsRuntimeException;
import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;
import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
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
 * implementation of AbstractJavaDbCatalog, redispatching to Lookup/DDL managers 
 * for Databases / Tables / Partitions / Functions
 */
@RequiredArgsConstructor
public class LookupsAndDdlsAdapterJavaDbCatalog<
	TDb extends DatabaseModel, 
	TTable extends TableModel, 
	TPart extends TablePartitionModel, 
	TFunc extends FunctionModel
	> extends AbstractJavaDbCatalog {

	private final CatalogModel2DtoConverter<TDb, TTable, TPart, TFunc> dtoConverter;
	private final DatabasesLookup<TDb> dbsLookup;
	private final DatabasesDDLManager<TDb> dbsDdl;
	private final TablesLookup<TDb, TTable> dbTablesLookup;
	private final TablesDDLManager<TDb, TTable> dbTablesDdl;
	private final TablePartitionsLookup<TDb, TTable, TPart> dbTablePartitionsLookup;
	private final TablePartitionsDDLManager<TDb, TTable, TPart> dbTablePartitionsDdl;
	private final FunctionsLookup<TDb, TFunc> dbFuncsLookup;
	private final FunctionsDDLManager<TDb, TFunc> dbFuncsDdl;
	private final DataLoaderManager<TDb,TTable,TPart> dataLoaderManager;

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

	protected TDb getDb(String dbName) {
		return dbsLookup.getDatabase(dbName);
	}

	@Override
	public void alterDatabase(String dbName, ImmutableCatalogDatabaseDef dbDef) {
		val db = getDb(dbName);
		dbsDdl.alterDatabase(db, dbDef);
	}

	@Override
	public CatalogDatabaseDTO getDatabase(String dbName) {
		val db = getDb(dbName);
		return dtoConverter.toDbDTO(db);
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
	public void createTable(CatalogTableDTO tableDefinition, boolean ignoreIfExists) {
		val tableId = tableDefinition.getIdentifier();
		String dbName = tableId.database;
		val db = getDb(dbName);
	    val tableName = tableId.table;
	    val found = dbTablesLookup.findTable(db, tableName);
	    if (null != found) {
	      if (!ignoreIfExists) {
	        throw new TableAlreadyExistsRuntimeException(dbName, tableName);
	      }
	    } else {
	    	val tbl = dbTablesDdl.createTable(db, tableDefinition, ignoreIfExists);
	    	dbTablesLookup.addTable(tbl);
	    }
	}

	@Override
	public void dropTable(String dbName, String tableName, boolean ignoreIfNotExists, boolean purge) {
		val db = getDb(dbName);
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

	protected TTable getTable(TDb db, String tableName) {
		return dbTablesLookup.getTable(db, tableName);
	}

	protected TTable doGetTable(String dbName, String tableName) {
		val db = getDb(dbName);
		return dbTablesLookup.getTable(db, tableName);
	}

	@Override
	public void renameTable(String dbName, String oldTableName, String newTableName) {
		val db = getDb(dbName);
		val table = dbTablesLookup.getTable(db, oldTableName);
		dbTablesLookup.requireTableNotExists(db, newTableName);
		val newTable = dbTablesDdl.renameTable(db, table, newTableName);
		dbTablesLookup.removePutTable(table, newTable);
	}

	@Override
	public void alterTable(CatalogTableDTO tableDefinition) {
		val tableId = tableDefinition.getIdentifier();
		String dbName = tableId.database;
		validate(dbName != null, "table database name not set");
		val db = getDb(dbName);
		val table = getTable(db, tableId.table);
		dbTablesDdl.alterTable(db, table, tableDefinition);
	}

	@Override
	public void alterTableDataSchema(String dbName, String tableName, 
			StructTypeDTO newDataSchema) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		dbTablesDdl.alterTableDataSchema(db, table, newDataSchema);
	}

	@Override
	public void alterTableStats(String dbName, String tableName, CatalogStatisticsDTO stats) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		dbTablesDdl.alterTableStats(db, table, stats);
	}

	@Override
	public CatalogTableDTO getTable(String db, String table) {
		val t = doGetTable(db, table);
		return dtoConverter.toTableDTO(t);
	}

	@Override
	public List<CatalogTableDTO> getTablesByName(String dbName, List<String> tableNames) {
		val db = getDb(dbName);
		return MetastoreListUtils.map(tableNames, n -> dtoConverter.toTableDTO(getTable(db, n)));
	}

	@Override
	public boolean tableExists(String dbName, String tableName) {
		val db = getDb(dbName);
		return dbTablesLookup.tableExists(db, tableName);
	}

	@Override
	public List<String> listTables(String dbName) {
		val db = getDb(dbName);
		return dbTablesLookup.listTables(db);
	}

	@Override
	public List<String> listTables(String dbName, String pattern) {
		val db = getDb(dbName);
		return dbTablesLookup.listTables(db, pattern);
	}

	@Override
	public void loadTable(String dbName, String tableName, String loadPath, boolean isOverwrite, boolean isSrcLocal) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		dataLoaderManager.loadTable(db, table, loadPath, isOverwrite, isSrcLocal);
	}

	// --------------------------------------------------------------------------
	// Partitions
	// --------------------------------------------------------------------------

	@Override
	public void createPartitions(String dbName, String tableName, 
			List<CatalogTablePartitionDTO> parts, boolean ignoreIfExists) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		val partModels = dbTablePartitionsDdl.createPartitions(db, table, parts, ignoreIfExists);
		dbTablePartitionsLookup.addPartitions(partModels);
	}

	@Override
	public void dropPartitions(String dbName, String tableName, 
				List<ImmutablePartitionSpec> partSpecs, boolean ignoreIfNotExists,
				boolean purge, boolean retainData) {
		val db = getDb(dbName);
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
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		validate(oldPartSpecs.size() == newSpecs.size(), "number of old and new partition specs differ");
		val oldPartModels = dbTablePartitionsLookup.getPartitions(db, table, oldPartSpecs);
		dbTablePartitionsLookup.requirePartitionsNotExist(db, table, newSpecs);
		List<TPart> newPartModels = dbTablePartitionsDdl.renamePartitions(db, table, oldPartModels, newSpecs);
		dbTablePartitionsLookup.removeAddPartitions(oldPartModels, newPartModels);
	}

	@Override
	public void alterPartitions(String dbName, String tableName, 
			List<CatalogTablePartitionDTO> partDefs) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		val partModels = dbTablePartitionsLookup.getPartitionByDefs(db, table, partDefs);
		dbTablePartitionsDdl.alterPartitions(db, table, partModels, partDefs);
	}

	@Override
	public CatalogTablePartitionDTO getPartition(String dbName, String tableName, ImmutablePartitionSpec spec) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		val tablePart = dbTablePartitionsLookup.getPartition(db, table, spec);
		return dtoConverter.toTablePartitionDTO(tablePart, table);
	}

	@Override
	public List<CatalogTablePartitionDTO> listPartitionsByPartialSpec(String dbName, String tableName, 
			ImmutablePartitionSpec partialSpec) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		val tableParts = dbTablePartitionsLookup.listPartitionsByPartialSpec(db, table, partialSpec);
		return dtoConverter.toTablePartitionDTOs(tableParts, table);
	}

	@Override
	public List<String> listPartitionNamesByPartialSpec(String dbName, String tableName, 
			ImmutablePartitionSpec partialSpec) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		val tableParts = dbTablePartitionsLookup.listPartitionsByPartialSpec(db, table, partialSpec);
		return MetastoreListUtils.map(tableParts, x -> x.getPartitionName());
	}

//	@Override
//	public List<CatalogTablePartitionDTO> listPartitionsByFilter(String dbName, String tableName, 
//			List<Expression> predicates,
//			String defaultTimeZoneId) {
//		val db = getDb(dbName);
//		val table = getTable(db, tableName);
//		val tableParts = dbTablePartitionsLookup.listPartitionsByFilter(db, table, predicates, defaultTimeZoneId);
//		return dtoConverter.toTablePartitionDTOs(tableParts, table);
//	}

	@Override
	public void loadPartition(String dbName, String tableName, String loadPath, 
			ImmutablePartitionSpec partSpec, boolean isOverwrite,
			boolean inheritTableSpecs, boolean isSrcLocal) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		val tablePart = dbTablePartitionsLookup.getPartition(db, table, partSpec);
		dataLoaderManager.loadPartition(db, table, tablePart, 
				loadPath, isOverwrite, inheritTableSpecs, isSrcLocal);
	}

	@Override
	public void loadDynamicPartitions(String dbName, String tableName, String loadPath, 
			ImmutablePartitionSpec partSpec,
			boolean replace, int numDP) {
		val db = getDb(dbName);
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
		val db = getDb(dbName);
		val found = dbFuncsLookup.findFunction(db, funcName);
		if (null == found) {
			throw new CatalogRuntimeException("Function already exists '" + dbName + "." + funcName + "'");
		}
		val func = dbFuncsDdl.createFunction(db, funcName, funcDef);
		dbFuncsLookup.add(func);
	}

	@Override
	public void dropFunction(String dbName, String funcName) {
		val db = getDb(dbName);
		val func = dbFuncsLookup.getFunction(db, funcName);
		dbFuncsDdl.dropFunction(db, func);
		dbFuncsLookup.remove(func);
	}

	@Override
	public void alterFunction(String dbName, String funcName, ImmutableCatalogFunctionDef funcDef) {
		val db = getDb(dbName);
		val func = dbFuncsLookup.getFunction(db, funcName);
		dbFuncsDdl.alterFunction(db, func, funcDef);
	}

	@Override
	public void renameFunction(String dbName, String oldFuncName, String newFuncName) {
		val db = getDb(dbName);
		val oldFunc = dbFuncsLookup.getFunction(db, oldFuncName);
		dbFuncsLookup.requireFunctionNotExists(db, newFuncName);
		val newFunc = dbFuncsDdl.renameFunction(db, oldFunc, newFuncName);
		dbFuncsLookup.removeAdd(oldFunc, newFunc);
	}
	
	@Override
	public CatalogFunctionDTO getFunction(String dbName, String funcName) {
		val db = getDb(dbName);
		val func = dbFuncsLookup.getFunction(db, funcName);
		return dtoConverter.toFunctionDTO(func);
	}

	@Override
	public boolean functionExists(String dbName, String funcName) {
		val db = getDb(dbName);
		return dbFuncsLookup.functionExists(db, funcName);
	}

	@Override
	public List<String> listFunctions(String dbName, String pattern) {
		val db = getDb(dbName);
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
