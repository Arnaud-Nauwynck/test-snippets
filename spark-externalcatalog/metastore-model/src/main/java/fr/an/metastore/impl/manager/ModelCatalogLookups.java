package fr.an.metastore.impl.manager;

import java.util.List;

import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import fr.an.metastore.api.spi.DatabasesLookup;
import fr.an.metastore.api.spi.FunctionsLookup;
import fr.an.metastore.api.spi.TablePartitionsLookup;
import fr.an.metastore.api.spi.TablesLookup;
import fr.an.metastore.impl.model.CatalogModel;
import fr.an.metastore.impl.model.DatabaseModel;
import fr.an.metastore.impl.model.FunctionModel;
import fr.an.metastore.impl.model.TableModel;
import fr.an.metastore.impl.model.TablePartitionModel;
import lombok.Getter;
import lombok.val;

/**
 * class containing default in-memory implementation of catalog Lookup on Databases | Tables | Partitions | Functions
 * 
 * delegating to corresponding model class DatabaseModel, TableModel
 * => to in-memory Map get() / keySet() / put() / remove() methods
 */
public class ModelCatalogLookups {

	private final CatalogModel dbCatalogModel;
	
	@Getter
	ModelDelegateDatabasesLookup dbsLookup = new ModelDelegateDatabasesLookup();

	@Getter
	ModelDelegateTablesLookup dbTablesLookup = new ModelDelegateTablesLookup();

	@Getter
	ModelDelegateTablePartitionsLookup dbTablePartitionsLookup = new ModelDelegateTablePartitionsLookup();
	
	@Getter
	ModelDelegateFunctionsLookup dbFuncsLookup = new ModelDelegateFunctionsLookup();

	// --------------------------------------------------------------------------------------------

	public ModelCatalogLookups(CatalogModel dbCatalogModel) {
		this.dbCatalogModel = dbCatalogModel;
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * implementation of DatabasesLookup, using in-memory DatabaseModel
	 */
	public class ModelDelegateDatabasesLookup extends DatabasesLookup<DatabaseModel> {

		@Override
		public DatabaseModel findDatabase(String db) {
			return dbCatalogModel.findDatabase(db);
		}

		@Override
		public List<String> listDatabases() {
			return dbCatalogModel.listDatabases();
		}

		@Override
		public void addDatabase(DatabaseModel db) {
			dbCatalogModel.addDatabase(db);
		}

		@Override
		public void removeDatabase(DatabaseModel db) {
			dbCatalogModel.removeDatabase(db);
		}

	}

	// --------------------------------------------------------------------------------------------

	/**
	 * implementation of DatabaseTablesLookup, delegating to DatabaseModel
	 */
	public class ModelDelegateTablesLookup extends TablesLookup<DatabaseModel,TableModel> {

		@Override
		public TableModel findTable(DatabaseModel db, String tableName) {
			return db.findTable(tableName);
		}

		@Override
		public List<String> listTables(DatabaseModel db) {
			return db.listTables();
		}

		@Override
		public void addTable(TableModel tbl) {
			val db = tbl.getDb();
			db.addTable(tbl);
		}

		@Override
		public void removeTable(TableModel tbl) {
			val db = tbl.getDb();
			db.removeTable(tbl);
		}
		
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * implementation of DatabaseTablePartitionsLookup delegating to TableModel
	 */
	public class ModelDelegateTablePartitionsLookup extends TablePartitionsLookup<DatabaseModel, TableModel, TablePartitionModel> {

		@Override
		public TablePartitionModel findPartition(DatabaseModel db, TableModel table, ImmutablePartitionSpec spec) {
			return table.findPartition(spec);
		}

		@Override
		public List<TablePartitionModel> listPartitionsByPartialSpec(DatabaseModel db, TableModel table, ImmutablePartitionSpec partialSpec) {
			return table.listPartitionsByPartialSpec(partialSpec);
		}

//		@Override
//		public List<TablePartitionModel> listPartitionsByFilter(DatabaseModel db, TableModel table,
//				List<Expression> predicates, String defaultTimeZoneId) {
//			return table.listPartitionsByFilter(predicates, defaultTimeZoneId);
//		}

		@Override
		public void addPartition(TablePartitionModel part) {
			val table = part.getTable();
			table.addPartition(part);
		}

		@Override
		public void removePartition(TablePartitionModel part) {
			val table = part.getTable();
			table.removePartition(part);
		}
		
	}
	
	// --------------------------------------------------------------------------------------------

	/**
	 * implementation of DatabaseFunctionsLookup delegating to DatabaseModel
	 */
	public class ModelDelegateFunctionsLookup extends FunctionsLookup<DatabaseModel, FunctionModel> {

		@Override
		public FunctionModel findFunction(DatabaseModel db, String funcName) {
			return db.findFunction(funcName);
		}

		@Override
		public List<String> listFunctions(DatabaseModel db) {
			return db.listFunctions();
		}

		@Override
		public void add(FunctionModel func) {
			val db = func.getDb();
			db.addFunction(func);
		}

		@Override
		public void remove(FunctionModel func) {
			val db = func.getDb();
			db.removeFunction(func);
		}
		
	}

}
