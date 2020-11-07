package fr.an.metastore.impl.manager;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import fr.an.metastore.api.dto.StructTypeDTO;
import fr.an.metastore.api.exceptions.CatalogWrappedRuntimeException;
import fr.an.metastore.api.immutable.CatalogFunctionId;
import fr.an.metastore.api.immutable.CatalogTableId;
import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;
import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogTableStatistics;
import fr.an.metastore.api.immutable.ImmutableCatalogTablePartitionDef;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import fr.an.metastore.api.spi.DatabasesDDL;
import fr.an.metastore.api.spi.FunctionsDDL;
import fr.an.metastore.api.spi.TablePartitionsDDL;
import fr.an.metastore.api.spi.TablesDDL;
import fr.an.metastore.api.utils.NotImpl;
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
public class ModelCatalogDDLs {

	private Configuration hadoopConfig;

	@Getter
	CatalogModel catalog;
	
	@Getter
	ModelDatabasesDDL dbsDdl = new ModelDatabasesDDL();

	@Getter
	ModelDatabaseTableDDL dbTablesDdl = new ModelDatabaseTableDDL();

	@Getter
	ModelTablePartitionsDDL dbTablePartitionsDdl = new ModelTablePartitionsDDL();
	
	@Getter
	ModelDatabaseFunctionsDDL dbFuncsDdl = new ModelDatabaseFunctionsDDL();

	// --------------------------------------------------------------------------------------------

	public ModelCatalogDDLs(CatalogModel catalog, Configuration hadoopConfig) {
		this.catalog = catalog;
		this.hadoopConfig = hadoopConfig;
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * implementation of DatabasesDDLManager, using in-memory DatabaseModel
	 */
	public class ModelDatabasesDDL extends DatabasesDDL<DatabaseModel> {
		
		@Override
		public DatabaseModel createDatabase(
				String dbName,
				ImmutableCatalogDatabaseDef dbDef,
				boolean ignoreIfExists) {
			ensureCreateLocationDir(dbDef.getLocationUri(), "create database " + dbName);
			val dbModel = new DatabaseModel(catalog, dbDef);
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
	public class ModelDatabaseTableDDL extends TablesDDL<DatabaseModel,TableModel> {

		@Override
		public TableModel createTable(DatabaseModel db, ImmutableCatalogTableDef tableDef, boolean ignoreIfExists) {
			val identifier = tableDef.getIdentifier();
//			val tableName = identifier.table;
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
			return new TableModel(db, tableDef);
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
			val newIdentifier = new CatalogTableId(db.getName(), newTableName);
			val defCopy = table.getDef().toBuilder();
			defCopy.identifier(newIdentifier);
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

			TableModel res = new TableModel(db, defCopy.build());
			return res;
		}

		@Override
		public void alterTable(DatabaseModel db, TableModel table, ImmutableCatalogTableDef tableDef) {
			table.setDef(tableDef);
		}

		@Override
		public void alterTableDataSchema(DatabaseModel db, TableModel table, StructTypeDTO newDataSchema) {
			throw NotImpl.notImplEx();
		}

		@Override
		public void alterTableStats(DatabaseModel db, TableModel table, ImmutableCatalogTableStatistics stats) {
			table.setStats(stats);
		}

	}

	// --------------------------------------------------------------------------------------------

	/**
	 * implementation of DatabaseTablePartitionsDDLManager using in-memory TablePartitionModel
	 */
	public class ModelTablePartitionsDDL extends TablePartitionsDDL<DatabaseModel, TableModel, TablePartitionModel> {

		@Override
		public List<TablePartitionModel> createPartitions(DatabaseModel db, TableModel table,
				List<ImmutableCatalogTablePartitionDef> parts, boolean ignoreIfExists) {
			throw NotImpl.notImplEx();
		}

		@Override
		public void dropPartitions(DatabaseModel db, TableModel table,
				List<TablePartitionModel> parts, 
				boolean ignoreIfNotExists, boolean purge, boolean retainData) {
			throw NotImpl.notImplEx();
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

			throw NotImpl.notImplEx();
		}

		@Override
		public void alterPartitions(DatabaseModel db, TableModel table, 
				List<TablePartitionModel> parts,		
				List<ImmutableCatalogTablePartitionDef> newPartDefs) {

			throw NotImpl.notImplEx();
			
		}

	}

	
	// --------------------------------------------------------------------------------------------

	/**
	 * implementation of DatabaseFunctionsDDLManager using in-memory FunctionModel
	 */
	public class ModelDatabaseFunctionsDDL extends FunctionsDDL<DatabaseModel, FunctionModel> {

		public FunctionModel createFunction(DatabaseModel db, ImmutableCatalogFunctionDef funcDef) {
			// do nothing, only manage memory model
			return new FunctionModel(db, funcDef);
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
		public FunctionModel renameFunction(DatabaseModel db, FunctionModel oldFunc, String newFuncName) {
			// do nothing, only manage memory model
			val newId = new CatalogFunctionId(db.getName(), newFuncName);
			val funcDef = oldFunc.getFuncDef().toBuilder()
					.identifier(newId)
					.build();
			return new FunctionModel(db, funcDef);
		}

	}

}
