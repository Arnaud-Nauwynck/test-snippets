package fr.an.dbcatalog.impl.manager;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.catalyst.FunctionIdentifier;
import org.apache.spark.sql.catalyst.TableIdentifier;
import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;
import org.apache.spark.sql.catalyst.catalog.CatalogFunction;
import org.apache.spark.sql.catalyst.catalog.CatalogStatistics;
import org.apache.spark.sql.catalyst.catalog.CatalogStorageFormat;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;
import org.apache.spark.sql.types.StructType;

import fr.an.dbcatalog.api.PartitionSpec;
import fr.an.dbcatalog.api.exceptions.CatalogWrappedRuntimeException;
import fr.an.dbcatalog.api.manager.DatabaseFunctionsDDLManager;
import fr.an.dbcatalog.api.manager.DatabaseTablePartitionsDDLManager;
import fr.an.dbcatalog.api.manager.DatabaseTablesDDLManager;
import fr.an.dbcatalog.api.manager.DatabasesDDLManager;
import fr.an.dbcatalog.impl.model.DatabaseModel;
import fr.an.dbcatalog.impl.model.FunctionModel;
import fr.an.dbcatalog.impl.model.TableModel;
import fr.an.dbcatalog.impl.model.TablePartitionModel;
import lombok.val;
import scala.Option;

public class InMemoryManagers {

	// --------------------------------------------------------------------------------------------

	public class InMemoryDatabasesDDLManager extends DatabasesDDLManager<DatabaseModel> {
		
		private Configuration hadoopConfig;

		@Override
		public DatabaseModel createDatabase(CatalogDatabase dbDefinition, boolean ignoreIfExists) {
			try {
				val location = new Path(dbDefinition.locationUri());
				val fs = location.getFileSystem(hadoopConfig);
				fs.mkdirs(location);
			} catch (IOException e) {
				throw new CatalogWrappedRuntimeException("Unable to create database " + dbDefinition.name() + " as failed " +
						"to create its directory " + dbDefinition.locationUri(), e);
			}
			val dbModel = new DatabaseModel();
			dbModel.setSparkDbDefinition(dbDefinition);
			return dbModel;
		}

		@Override
		public void dropDatabase(DatabaseModel db, boolean ignoreIfNotExists, boolean cascade) {
			// Remove the database.
			val dbDefinition = db.getSparkDbDefinition();
			try {
				val location = new Path(dbDefinition.locationUri());
				val fs = location.getFileSystem(hadoopConfig);
				fs.delete(location, true);
			} catch (IOException e) {
				throw new CatalogWrappedRuntimeException("Unable to drop database " + dbDefinition.name() + " as failed " +
						"to delete its directory " + dbDefinition.locationUri(), e);
			}
		}

		@Override
		public void alterDatabase(DatabaseModel db, CatalogDatabase dbDefinition) {
			db.setSparkDbDefinition(dbDefinition);
		}

	}


	public abstract class InMemoryDatabaseTableDDLManager extends DatabaseTablesDDLManager<DatabaseModel,TableModel> {

		@Override
		public TableModel createTable(DatabaseModel db, CatalogTable tableDefinition, boolean ignoreIfExists) {
			val tableName = tableDefinition.identifier().table();
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
			return new TableModel(db, tableName, tableDefinition);
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
		    val t = table.getSparkTableDefinition();
			val newIdentifier = new TableIdentifier(newTableName, scala.Option.apply(db.getName()));
			// TODO
//		    oldDesc.table = oldDesc.table.copy())

			CatalogStorageFormat newStorage = t.storage();
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
			
			val newSparkTableDefinition = new CatalogTable(newIdentifier, 
					t.tableType(), newStorage, t.schema(), t.provider(), t.partitionColumnNames(), 
					t.bucketSpec(), t.owner(), 
					t.createTime(), t.lastAccessTime(), t.createVersion(), 
					t.properties(), t.stats(), t.viewText(), t.comment(), 
					t.unsupportedFeatures(), t.tracksPartitionsInCatalog(), t.schemaPreservesCase(), 
					t.ignoredProperties(), t.viewOriginalText());
			return new TableModel(db, newTableName, newSparkTableDefinition);
		}

		@Override
		public void alterTable(DatabaseModel db, TableModel table, CatalogTable tableDefinition) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void alterTableDataSchema(DatabaseModel db, TableModel table, StructType newDataSchema) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void alterTableStats(DatabaseModel db, TableModel table, CatalogStatistics stats) {
			// TODO Auto-generated method stub
			
		}
		

	}
	
	// --------------------------------------------------------------------------------------------

	public class InMemoryDatabaseFunctionsDDLManager extends DatabaseFunctionsDDLManager<DatabaseModel, FunctionModel> {

		public FunctionModel createFunction(DatabaseModel db, CatalogFunction funcDef) {
			String funcName = funcDef.identifier().funcName();
			return new FunctionModel(db, funcName, funcDef);
		}

		public void dropFunction(DatabaseModel db, FunctionModel func) {
			// do nothing
		}

		public void alterFunction(DatabaseModel db, FunctionModel func, CatalogFunction funcDef) {
			func.setSparkFunctionDefinition(funcDef);			
		}

		public FunctionModel renameFunction(DatabaseModel db, FunctionModel func, String newFuncName) {
			String dbName = db.getName();
			val f = func.getSparkFunctionDefinition();
			FunctionIdentifier newId = new FunctionIdentifier(newFuncName, Option.apply(dbName));
			val newSparkFunc = new CatalogFunction(newId, f.className(), 
					f.resources() // copy?
					);
			return new FunctionModel(db, newFuncName, newSparkFunc);
		}

	}

	// --------------------------------------------------------------------------------------------

	public class InMemoryTablePartitionDDLManager extends DatabaseTablePartitionsDDLManager<DatabaseModel, TableModel, TablePartitionModel> {

		@Override
		public List<TablePartitionModel> createPartitions(DatabaseModel db, TableModel table,
				List<CatalogTablePartition> parts, boolean ignoreIfExists) {
			throw new UnsupportedOperationException("TODO NOT IMPLEMENTED YET");
		}

		@Override
		public void dropPartitions(DatabaseModel db, TableModel table,
				List<TablePartitionModel> parts, 
				boolean ignoreIfNotExists, boolean purge, boolean retainData) {
			throw new UnsupportedOperationException("TODO NOT IMPLEMENTED YET");
		}

		@Override
		public List<TablePartitionModel> renamePartitions(DatabaseModel db, TableModel table, List<TablePartitionModel> parts,
				List<PartitionSpec> newSpecs) {
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
				List<CatalogTablePartition> newPartDefs) {

			throw new UnsupportedOperationException("TODO NOT IMPLEMENTED YET");
			
		}

		

	}
}
