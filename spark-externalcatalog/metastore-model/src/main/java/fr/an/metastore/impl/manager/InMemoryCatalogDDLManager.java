package fr.an.metastore.impl.manager;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import fr.an.metastore.api.dto.CatalogTableDTO;
import fr.an.metastore.api.dto.CatalogTableDTO.CatalogStatisticsDTO;
import fr.an.metastore.api.dto.CatalogTablePartitionDTO;
import fr.an.metastore.api.dto.StructTypeDTO;
import fr.an.metastore.api.exceptions.CatalogWrappedRuntimeException;
import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;
import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import fr.an.metastore.api.immutable.ImmutableTableId;
import fr.an.metastore.api.manager.DatabasesDDLManager;
import fr.an.metastore.api.manager.FunctionsDDLManager;
import fr.an.metastore.api.manager.TablePartitionsDDLManager;
import fr.an.metastore.api.manager.TablesDDLManager;
import fr.an.metastore.impl.model.CatalogModel;
import fr.an.metastore.impl.model.DatabaseModel;
import fr.an.metastore.impl.model.FunctionModel;
import fr.an.metastore.impl.model.TableModel;
import fr.an.metastore.impl.model.TablePartitionModel;
import lombok.Getter;
import lombok.val;

/**
 * class containing default implementations of DDLs for Databases | Tables | Partitions | Functions
 */
public class InMemoryCatalogDDLManager {

	private Configuration hadoopConfig;

	@Getter
	CatalogModel catalog;
	
	@Getter
	InMemoryDatabasesDDLManager dbsDdl = new InMemoryDatabasesDDLManager();

	@Getter
	InMemoryDatabaseTableDDLManager dbTablesDdl = new InMemoryDatabaseTableDDLManager();

	@Getter
	InMemoryTablePartitionsDDLManager dbTablePartitionsDdl = new InMemoryTablePartitionsDDLManager();
	
	@Getter
	InMemoryDatabaseFunctionsDDLManager dbFuncsDdl = new InMemoryDatabaseFunctionsDDLManager();

	// --------------------------------------------------------------------------------------------

	public InMemoryCatalogDDLManager(CatalogModel catalog, Configuration hadoopConfig) {
		this.catalog = catalog;
		this.hadoopConfig = hadoopConfig;
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * implementation of DatabasesDDLManager, using in-memory DatabaseModel
	 */
	public class InMemoryDatabasesDDLManager extends DatabasesDDLManager<DatabaseModel> {
		
		@Override
		public DatabaseModel createDatabase(
				String dbName,
				ImmutableCatalogDatabaseDef dbDef,
				boolean ignoreIfExists) {
			ensureCreateLocationDir(dbDef.getLocationUri(), "create database " + dbName);
			val dbModel = new DatabaseModel(catalog, dbName, dbDef);
			return dbModel;
		}

		private void ensureCreateLocationDir(URI locationUri, String displayMsg) {
			try {
				val location = new Path(locationUri);
				val fs = location.getFileSystem(hadoopConfig);
				if (!fs.exists(location)) {
					fs.mkdirs(location);
				} // ensure is dir?
			} catch (IOException e) {
				throw new CatalogWrappedRuntimeException("Unable to " + displayMsg + " as failed " +
						"to create its directory " + locationUri, e);
			}
		}

		@Override
		public void dropDatabase(DatabaseModel db, boolean ignoreIfNotExists, boolean cascade) {
			// Remove the database??
			val locationUri = db.getDbDef().getLocationUri();
			try {
				val location = new Path(locationUri);
				val fs = location.getFileSystem(hadoopConfig);
				fs.delete(location, true);
			} catch (IOException e) {
				throw new CatalogWrappedRuntimeException("Unable to drop database " + db.getName() + " as failed " +
						"to delete its directory " + locationUri, e);
			}
		}

		@Override
		public void alterDatabase(DatabaseModel db, ImmutableCatalogDatabaseDef dbDef) {
			URI oldLocationUri = db.getDbDef().getLocationUri();
			URI newLocationUri = dbDef.getLocationUri();
			if (! newLocationUri.equals(oldLocationUri)) {
				ensureCreateLocationDir(newLocationUri, "alter database " + db.getName());
			}
			db.setDbDef(dbDef);
		}

	}

	// --------------------------------------------------------------------------------------------

	/**
	 * implementation of DatabaseTablesDDLManager using in-memory TableModel
	 */
	public class InMemoryDatabaseTableDDLManager extends TablesDDLManager<DatabaseModel,TableModel> {

		@Override
		public TableModel createTable(DatabaseModel db, CatalogTableDTO tableDef, boolean ignoreIfExists) {
			val identifier = tableDef.getIdentifier();
			val tableName = identifier.table;
//		      // Set the default table location if this is a managed table and its location is not specified.
//		      // Ideally we should not create a managed table with location, but Hive serde table can
//		      // specify location for managed table. And in [[CreateDataSourceTableAsSelectCommand]] we have
//		      // to create the table directory and write out data before we create this table, to avoid
//		      // exposing a partial written table.
//		      val needDefaultTableLocation =
//		        tableDefinition.tableType == CatalogTableType.MANAGED &&
//		          tableDefinition.storage.locationUri.isEmpty
	//
//		      val tableWithLocation = if (needDefaultTableLocation) {
//		        val defaultTableLocation = new Path(new Path(catalog(db).db.locationUri), table)
//		        try {
//		          val fs = defaultTableLocation.getFileSystem(hadoopConfig)
//		          fs.mkdirs(defaultTableLocation)
//		        } catch {
//		          case e: IOException =>
//		            throw new SparkException(s"Unable to create table $table as failed " +
//		              s"to create its directory $defaultTableLocation", e)
//		        }
//		        tableDefinition.withNewStorage(locationUri = Some(defaultTableLocation.toUri))
//		      } else {
//		        tableDefinition
//		      }
//		      val tableProp = tableWithLocation.properties.filter(_._1 != "comment")
//			tableWithLocation.copy(properties = tableProp)
			return new TableModel(db, tableName, 
					tableDef.getTableType(),
					tableDef.getPartitionColumnNames());
		}

		@Override
		public void dropTable(DatabaseModel db, TableModel table, boolean ignoreIfNotExists, boolean purge) {
//	      val tableMeta = getTable(db, table)
//	      if (tableMeta.tableType == CatalogTableType.MANAGED) {
//	        // Delete the data/directory for each partition
//	        val locationAllParts = catalog(db).tables(table).partitions.values.toSeq.map(_.location)
//	        locationAllParts.foreach { loc =>
//	          val partitionPath = new Path(loc)
//	          try {
//	            val fs = partitionPath.getFileSystem(hadoopConfig)
//	            fs.delete(partitionPath, true)
//	          } catch {
//	            case e: IOException =>
//	              throw new SparkException(s"Unable to delete partition path $partitionPath", e)
//	          }
//	        }
//	        assert(tableMeta.storage.locationUri.isDefined,
//	          "Managed table should always have table location, as we will assign a default location " +
//	            "to it if it doesn't have one.")
//	        // Delete the data/directory of the table
//	        val dir = new Path(tableMeta.location)
//	        try {
//	          val fs = dir.getFileSystem(hadoopConfig)
//	          fs.delete(dir, true)
//	        } catch {
//	          case e: IOException =>
//	            throw new SparkException(s"Unable to drop table $table as failed " +
//	              s"to delete its directory $dir", e)
//	        }
//	      }
			
		}

		@Override
		public TableModel renameTable(DatabaseModel db, TableModel table, String newTableName) {
			val newIdentifier = new ImmutableTableId(db.getName(), newTableName);
			// TODO

//		    if (oldDesc.table.tableType == CatalogTableType.MANAGED) {
//		      assert(oldDesc.table.storage.locationUri.isDefined,
//		        "Managed table should always have table location, as we will assign a default location " +
//		          "to it if it doesn't have one.")
//		      val oldDir = new Path(oldDesc.table.location)
//		      val newDir = new Path(new Path(catalog(db).db.locationUri), newName)
//		      try {
//		        val fs = oldDir.getFileSystem(hadoopConfig)
//		        fs.rename(oldDir, newDir)
//		      } catch {
//		        case e: IOException =>
//		          throw new SparkException(s"Unable to rename table $oldName to $newName as failed " +
//		            s"to rename its directory $oldDir", e)
//		      }
//		      oldDesc.table = oldDesc.table.withNewStorage(locationUri = Some(newDir.toUri))
//		    }
		//
			List<String> partitionColumns = table.getPartitionColumnNames();
			TableModel res = new TableModel(db, newTableName, table.getTableType(), 
					partitionColumns);
			// TODO
			return res;
		}

		@Override
		public void alterTable(DatabaseModel db, TableModel table, CatalogTableDTO tableDefinition) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void alterTableDataSchema(DatabaseModel db, TableModel table, StructTypeDTO newDataSchema) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void alterTableStats(DatabaseModel db, TableModel table, CatalogStatisticsDTO stats) {
			// TODO Auto-generated method stub
			
		}

	}

	// --------------------------------------------------------------------------------------------

	/**
	 * implementation of DatabaseTablePartitionsDDLManager using in-memory TablePartitionModel
	 */
	public class InMemoryTablePartitionsDDLManager extends TablePartitionsDDLManager<DatabaseModel, TableModel, TablePartitionModel> {

		@Override
		public List<TablePartitionModel> createPartitions(DatabaseModel db, TableModel table,
				List<CatalogTablePartitionDTO> parts, boolean ignoreIfExists) {
			throw new UnsupportedOperationException("TODO NOT IMPLEMENTED YET");
		}

		@Override
		public void dropPartitions(DatabaseModel db, TableModel table,
				List<TablePartitionModel> parts, 
				boolean ignoreIfNotExists, boolean purge, boolean retainData) {
			throw new UnsupportedOperationException("TODO NOT IMPLEMENTED YET");
		}

		@Override
		public List<TablePartitionModel> renamePartitions(DatabaseModel db, TableModel table, 
				List<TablePartitionModel> parts,
				List<ImmutablePartitionSpec> newSpecs) {
//			CatalogTable tableMeta = table.getSparkTableDefinition();
//		    val partitionColumnNames = tableMeta.partitionColumnNames();
//		    val tablePath = new Path(tableMeta.location)
//		    val shouldUpdatePartitionLocation = getTable(db, table).tableType == CatalogTableType.MANAGED
//		    val existingParts = catalog(db).tables(table).partitions
//		    // TODO: we should follow hive to roll back if one partition path failed to rename.
//		    specs.zip(newSpecs).foreach { case (oldSpec, newSpec) =>
//		      val oldPartition = getPartition(db, table, oldSpec)
//		      val newPartition = if (shouldUpdatePartitionLocation) {
//		        val oldPartPath = new Path(oldPartition.location)
//		        val newPartPath = ExternalCatalogUtils.generatePartitionPath(
//		          newSpec, partitionColumnNames, tablePath)
//		        try {
//		          val fs = tablePath.getFileSystem(hadoopConfig)
//		          fs.rename(oldPartPath, newPartPath)
//		        } catch {
//		          case e: IOException =>
//		            throw new SparkException(s"Unable to rename partition path $oldPartPath", e)
//		        }
//		        oldPartition.copy(
//		          spec = newSpec,
//		          storage = oldPartition.storage.copy(locationUri = Some(newPartPath.toUri)))
//		      } else {
//		        oldPartition.copy(spec = newSpec)
//		      }

			throw new UnsupportedOperationException("TODO NOT IMPLEMENTED YET");
		}

		@Override
		public void alterPartitions(DatabaseModel db, TableModel table, 
				List<TablePartitionModel> parts,				
				List<CatalogTablePartitionDTO> newPartDefs) {

			throw new UnsupportedOperationException("TODO NOT IMPLEMENTED YET");
			
		}

	}

	
	// --------------------------------------------------------------------------------------------

	/**
	 * implementation of DatabaseFunctionsDDLManager using in-memory FunctionModel
	 */
	public class InMemoryDatabaseFunctionsDDLManager extends FunctionsDDLManager<DatabaseModel, FunctionModel> {

		public FunctionModel createFunction(DatabaseModel db, String funcName, ImmutableCatalogFunctionDef funcDef) {
			// do nothing, only manage memory model
			return new FunctionModel(db, funcName, funcDef);
		}

		@Override
		public void dropFunction(DatabaseModel db, FunctionModel func) {
			// do nothing, only manage memory model
		}

		@Override
		public void alterFunction(DatabaseModel db, FunctionModel func, ImmutableCatalogFunctionDef funcDef) {
			// do nothing, only manage memory model
			func.setFuncDef(funcDef);			
		}

		@Override
		public FunctionModel renameFunction(DatabaseModel db, FunctionModel func, String newFuncName) {
			// do nothing, only manage memory model
			val funcDef = func.getFuncDef();
			return new FunctionModel(db, newFuncName, funcDef);
		}

	}

}
