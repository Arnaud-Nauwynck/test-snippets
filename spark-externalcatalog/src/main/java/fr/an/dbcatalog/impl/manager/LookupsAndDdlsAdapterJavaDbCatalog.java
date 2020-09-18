package fr.an.dbcatalog.impl.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.apache.spark.sql.catalyst.TableIdentifier;
import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;
import org.apache.spark.sql.catalyst.catalog.CatalogFunction;
import org.apache.spark.sql.catalyst.catalog.CatalogStatistics;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;
import org.apache.spark.sql.catalyst.expressions.Expression;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import fr.an.dbcatalog.api.AbstractJavaDbCatalog;
import fr.an.dbcatalog.api.PartitionSpec;
import fr.an.dbcatalog.api.exceptions.CatalogRuntimeException;
import fr.an.dbcatalog.api.exceptions.NoSuchTableCatalogRuntimeException;
import fr.an.dbcatalog.api.exceptions.TableAlreadyExistsRuntimeException;
import fr.an.dbcatalog.api.manager.DataLoaderManager;
import fr.an.dbcatalog.api.manager.FunctionsDDLManager;
import fr.an.dbcatalog.api.manager.FunctionsLookup;
import fr.an.dbcatalog.api.manager.TablePartitionsDDLManager;
import fr.an.dbcatalog.api.manager.TablePartitionsLookup;
import fr.an.dbcatalog.api.manager.TablesDDLManager;
import fr.an.dbcatalog.api.manager.TablesLookup;
import fr.an.dbcatalog.api.manager.DatabasesDDLManager;
import fr.an.dbcatalog.api.manager.DatabasesLookup;
import fr.an.dbcatalog.impl.model.DatabaseModel;
import fr.an.dbcatalog.impl.model.FunctionModel;
import fr.an.dbcatalog.impl.model.TableModel;
import fr.an.dbcatalog.impl.model.TablePartitionModel;
import fr.an.dbcatalog.impl.utils.ListUtils;
import fr.an.dbcatalog.spark.util.CatalogTableBuilder;
import lombok.RequiredArgsConstructor;
import lombok.val;
import scala.Option;

/**
 * implementation of AbstractJavaDbCatalog, redispatching to Lookup/DDL managers 
 * for Databases / Tables / Partitions / Functions
 */
@RequiredArgsConstructor
public class LookupsAndDdlsAdapterJavaDbCatalog<
	TDb extends DatabaseModel, TTable extends TableModel, TPart extends TablePartitionModel, TFunc extends FunctionModel
	> extends AbstractJavaDbCatalog {

	private final DatabasesLookup<TDb> dbsLookup;
	private final DatabasesDDLManager<TDb> dbsDdl;
	private final TablesLookup<TDb, TTable> dbTablesLookup;
	private final TablesDDLManager<TDb, TTable> dbTablesDdl;
	private final TablePartitionsLookup<TDb, TTable, TPart> dbTablePartitionsLookup;
	private final TablePartitionsDDLManager<TDb, TTable, TPart> dbTablePartitionsDdl;
	private final FunctionsLookup<TDb, TFunc> dbFuncsLookup;
	private final FunctionsDDLManager<TDb, TFunc> dbFuncsDdl;
	private final DataLoaderManager<TDb,TTable> dataLoadManager;

	String currentDatabase = "default";

	// --------------------------------------------------------------------------
	// Databases
	// --------------------------------------------------------------------------

	@Override
	public void setCurrentDatabase(String db) {
		this.currentDatabase = db;
	}

	@Override
	public synchronized void createDatabase(CatalogDatabase dbDefinition, boolean ignoreIfExists) {
		val dbName = dbDefinition.name();
		val found = dbsLookup.findDatabase(dbName);
		if (null != found) {
	        if (!ignoreIfExists) {
	        	throw new CatalogRuntimeException("Database already exists '" + dbName + "'");
	        }
		} else {
			val db = dbsDdl.createDatabase(dbDefinition, ignoreIfExists);
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
	public void alterDatabase(CatalogDatabase dbDefinition) {
		val dbName = dbDefinition.name();
		val db = getDb(dbName);
		dbsDdl.alterDatabase(db, dbDefinition);
	}

	@Override
	public CatalogDatabase getDatabase(String dbName) {
		val db = getDb(dbName);
		return db.getSparkDbDefinition();
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
	public void createTable(CatalogTable tableDefinition, boolean ignoreIfExists) {
		TableIdentifier tableId = tableDefinition.identifier();
		String dbName = tableId.database().get();
		val db = getDb(dbName);
	    val tableName = tableId.table();
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
				throw new NoSuchTableCatalogRuntimeException(dbName, tableName);
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
	public void alterTable(CatalogTable tableDefinition) {
		val tableId = tableDefinition.identifier();
		scala.Option<String> optDbName = tableId.database();
		validate(optDbName.isDefined() && optDbName.get() != null, "table database name not set");
		val dbName = optDbName.get();
		val db = getDb(dbName);
		val table = getTable(db, tableId.table());
		dbTablesDdl.alterTable(db, table, tableDefinition);
		
	    scala.collection.immutable.Map<String,String> updatedProperties = tableDefinition.properties();
	    		// TODO !!!!
//	    		(tableDefinition.properties()).filter(
//	    		ScalaCollUtils.toScalaFunc(kv -> kv._1 != "comment"));
	    val defBuilder = new CatalogTableBuilder(table.getSparkTableDefinition());
	    defBuilder.setProperties(updatedProperties);
	    val newTableDefinition = defBuilder.build();
		table.setSparkTableDefinition(newTableDefinition);
	}

	@Override
	public void alterTableDataSchema(String dbName, String tableName, StructType newDataSchema) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		dbTablesDdl.alterTableDataSchema(db, table, newDataSchema);
		
		val defBuilder = new CatalogTableBuilder(table.getSparkTableDefinition());
		StructType prevPartionSchema = table.getSparkTableDefinition().partitionSchema();
		List<StructField> newFields = new ArrayList<>();
		newFields.addAll(Arrays.asList(newDataSchema.fields()));
		newFields.addAll(Arrays.asList(prevPartionSchema.fields()));
		val newSchema = new StructType(newFields.toArray(new StructField[newFields.size()]));
		defBuilder.setSchema(newSchema);
		val newSparkTableDef = defBuilder.build();
		table.setSparkTableDefinition(newSparkTableDef);
	}

	@Override
	public void alterTableStats(String dbName, String tableName, CatalogStatistics stats) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		dbTablesDdl.alterTableStats(db, table, stats);
		
		val defBuilder = new CatalogTableBuilder(table.getSparkTableDefinition());
		defBuilder.setStats(Option.apply(stats));
		val newSparkTableDef = defBuilder.build();
		table.setSparkTableDefinition(newSparkTableDef);
	}

	@Override
	public CatalogTable getTable(String db, String table) {
		val t = doGetTable(db, table);
		return t.getSparkTableDefinition();
	}

	@Override
	public List<CatalogTable> getTablesByName(String dbName, List<String> tableNames) {
		val db = getDb(dbName);
		return ListUtils.map(tableNames, n -> getTable(db, n).getSparkTableDefinition());
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
		dataLoadManager.loadTable(db, table, loadPath, isOverwrite, isSrcLocal);
	}

	// --------------------------------------------------------------------------
	// Partitions
	// --------------------------------------------------------------------------

	@Override
	public void createPartitions(String dbName, String tableName, List<CatalogTablePartition> parts, boolean ignoreIfExists) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		val partModels = dbTablePartitionsDdl.createPartitions(db, table, parts, ignoreIfExists);
		dbTablePartitionsLookup.addPartitions(partModels);
	}

	@Override
	public void dropPartitions(String dbName, String tableName, List<PartitionSpec> partSpecs, boolean ignoreIfNotExists,
			boolean purge, boolean retainData) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		val partModels = dbTablePartitionsLookup.getPartitions(db, table, partSpecs);
		dbTablePartitionsDdl.dropPartitions(db, table, partModels,
				ignoreIfNotExists, purge, retainData);
		dbTablePartitionsLookup.removePartitions(partModels);
	}

	@Override
	public void renamePartitions(String dbName, String tableName, List<PartitionSpec> oldPartSpecs, List<PartitionSpec> newSpecs) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		validate(oldPartSpecs.size() == newSpecs.size(), "number of old and new partition specs differ");
		val oldPartModels = dbTablePartitionsLookup.getPartitions(db, table, oldPartSpecs);
		dbTablePartitionsLookup.requirePartitionsNotExist(db, table, newSpecs);
		List<TPart> newPartModels = dbTablePartitionsDdl.renamePartitions(db, table, oldPartModels, newSpecs);
		dbTablePartitionsLookup.removeAddPartitions(oldPartModels, newPartModels);
	}

	@Override
	public void alterPartitions(String dbName, String tableName, List<CatalogTablePartition> partDefs) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		val partModels = dbTablePartitionsLookup.getPartitionByDefs(db, table, partDefs);
		dbTablePartitionsDdl.alterPartitions(db, table, partModels, partDefs);
		
		for(int i = 0, len = partDefs.size(); i < len; i++) {
			val partDef = partDefs.get(i);
			val m = partModels.get(i);
			m.setSparkDefinition(partDef);
		}
	}

	@Override
	public CatalogTablePartition getPartition(String dbName, String tableName, PartitionSpec spec) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		val tmpres = dbTablePartitionsLookup.getPartition(db, table, spec);
		return tmpres.getSparkDefinition();
	}

	@Override
	public List<CatalogTablePartition> listPartitions(String dbName, String tableName, PartitionSpec partialSpec) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		val tmpres = dbTablePartitionsLookup.listPartitions(db, table, partialSpec);
		return ListUtils.map(tmpres, x -> x.getSparkDefinition());
	}

	@Override
	public List<String> listPartitionNames(String dbName, String tableName, PartitionSpec spec) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		return dbTablePartitionsLookup.listPartitionNames(db, table, spec);
	}

	@Override
	public List<CatalogTablePartition> listPartitionsByFilter(String dbName, String tableName, List<Expression> predicates,
			String defaultTimeZoneId) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		val tmpres = dbTablePartitionsLookup.listPartitionsByFilter(db, table, predicates, defaultTimeZoneId);
		return ListUtils.map(tmpres, x -> x.getSparkDefinition());
	}

	@Override
	public void loadPartition(String dbName, String tableName, String loadPath, PartitionSpec partition, boolean isOverwrite,
			boolean inheritTableSpecs, boolean isSrcLocal) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		dataLoadManager.loadPartition(db, table, loadPath, partition, isOverwrite, inheritTableSpecs, isSrcLocal);
	}

	@Override
	public void loadDynamicPartitions(String dbName, String tableName, String loadPath, PartitionSpec partition,
			boolean replace, int numDP) {
		val db = getDb(dbName);
		val table = getTable(db, tableName);
		dataLoadManager.loadDynamicPartitions(db, table, loadPath, partition, replace, numDP);
	}

	// --------------------------------------------------------------------------
	// Functions
	// --------------------------------------------------------------------------

	@Override
	public void createFunction(String dbName, CatalogFunction sparkFuncDef) {
		val db = getDb(dbName);
		String funcName = sparkFuncDef.identifier().funcName();
		val found = dbFuncsLookup.findFunction(db, funcName);
		if (null == found) {
			throw new CatalogRuntimeException("Function already exists '" + dbName + "." + funcName + "'");
		}
		val func = dbFuncsDdl.createFunction(db, sparkFuncDef);
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
	public void alterFunction(String dbName, CatalogFunction sparkFuncDef) {
		val db = getDb(dbName);
		String funcName = sparkFuncDef.identifier().funcName();
		val func = dbFuncsLookup.getFunction(db, funcName);
		dbFuncsDdl.alterFunction(db, func, sparkFuncDef);
		func.setSparkFunctionDefinition(sparkFuncDef);
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
	public CatalogFunction getFunction(String dbName, String funcName) {
		val db = getDb(dbName);
		val func = dbFuncsLookup.getFunction(db, funcName);
		return func.getSparkFunctionDefinition();
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
