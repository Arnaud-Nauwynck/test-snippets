package fr.an.dbcatalog.spark;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.catalyst.FunctionIdentifier;
import org.apache.spark.sql.catalyst.analysis.TableAlreadyExistsException;
import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;
import org.apache.spark.sql.catalyst.catalog.CatalogFunction;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;
import org.apache.spark.sql.catalyst.catalog.ExternalCatalogUtils;
import org.apache.spark.sql.catalyst.util.StringUtils;

import fr.an.dbcatalog.api.exceptions.CatalogRuntimeException;
import fr.an.dbcatalog.api.exceptions.CatalogWrappedRuntimeException;
import fr.an.dbcatalog.spark.util.ScalaCollUtils;
import lombok.val;
import scala.Option;
import scala.collection.Seq;
import scala.collection.Set;
import scala.collection.mutable.ArrayBuffer;

public abstract class InMemoryCatalog extends AbstractExternalCatalog {
	
	private SparkConf conf;
	
	private Configuration hadoopConfig;
	
	private static class TableDesc {
	  CatalogTable table;
	  scala.collection.mutable.HashMap<scala.collection.immutable.Map<String,String>, CatalogTablePartition> partitions = 
			  new scala.collection.mutable.HashMap<scala.collection.immutable.Map<String,String>, CatalogTablePartition>();
	}

  // @RequiredArgsConstructor
  private static class DatabaseDesc {
	  CatalogDatabase db;
	  scala.collection.mutable.HashMap<String, TableDesc> tables = new scala.collection.mutable.HashMap<String, TableDesc>();
	  scala.collection.mutable.HashMap<String, CatalogFunction> functions = new scala.collection.mutable.HashMap<String, CatalogFunction>();

	  public TableDesc getTable(String table) {
		  Option<TableDesc> opt = tables.get(table);
		  if (opt.isEmpty()) {
			  throw new CatalogRuntimeException("No such table '" + db.name() + "." + table + "'");
		  }
		  return opt.get();
	  }

	  public void requireFunctionExists(String funcName) {
		  getFunction(funcName);
	  }
	  public void requireFunctionNotExists(String funcName) {
    	  throw new CatalogRuntimeException("Function already exists '" + db.name() + "." + funcName + "'");
	  }
	  
	  private CatalogFunction getFunction(String funcName) {
		  Option<CatalogFunction> opt = functions.get(funcName);
		  if (opt.isEmpty()) {
			  throw new CatalogRuntimeException("No such function '" + db.name() + "." + funcName + "'");
		  }
		  return opt.get();
	  }

  }

  // Database name -> description
  private scala.collection.mutable.HashMap<String, DatabaseDesc> catalog = new scala.collection.mutable.HashMap<String, DatabaseDesc>();

  private boolean partitionExists(String db, String table, 
		  scala.collection.immutable.Map<String,String> spec) {
    requireTableExists(db, table);
    DatabaseDesc dbDesc = catalog.get(db).get();
	TableDesc tableDesc = dbDesc.tables.get(table).get();
	return tableDesc.partitions.contains(spec);
  }

  private DatabaseDesc getDb(String db) {
	  Option<DatabaseDesc> opt = catalog.get(db);
	  if (opt.isEmpty()) {
		  throw new CatalogRuntimeException("No such database '" + db + "'");
	  }
	  return opt.get();
  }

  private TableDesc tableOf(String db, String table) {
	  DatabaseDesc dbDesc = getDb(db);
	  return dbDesc.getTable(table);
  }

  
  private void requireTableNotExists(String db, String table) throws TableAlreadyExistsException {
		if (tableExists(db, table)) {
			throw new TableAlreadyExistsException(db, table);
		}
  }

  private void requirePartitionsExist(
      String db,
      String table,
      Seq<scala.collection.immutable.Map<String,String>> specs
      ) {
	  ScalaCollUtils.foreach(specs, s -> {
	      if (!partitionExists(db, table, s)) {
	    	  throw new RuntimeException(
	    			  "Partition not found in table '" + table + "' database '" + db + "':\n" + s.mkString("\n"));
//	    	   throw new NoSuchPartitionException(db, table, s);
	      }
	  });
  }

  private void requirePartitionsNotExist(
      String db,
      String table,
      Seq<scala.collection.immutable.Map<String,String>> specs) {
	  ScalaCollUtils.foreach(specs, s -> {
		  if (partitionExists(db, table, s)) {
	    	  throw new CatalogRuntimeException(
	    			  "Partition already exists in table '" + table + "' database '" + db + "':\n" + s.mkString("\n"));
			  // throw new PartitionAlreadyExistsException(db, table, s);
		  }
	  });
  }

  // --------------------------------------------------------------------------
  // Databases
  // --------------------------------------------------------------------------

  @Override 
  public synchronized void createDatabase(
		  CatalogDatabase dbDefinition,
		  boolean ignoreIfExists
		  ) {
    if (catalog.contains(dbDefinition.name())) {
      if (!ignoreIfExists) {
    	  throw new CatalogRuntimeException("Database already exists '" + dbDefinition.name() + "'");
      }
    } else {
      try {
        val location = new Path(dbDefinition.locationUri());
        val fs = location.getFileSystem(hadoopConfig);
        fs.mkdirs(location);
      } catch (IOException e) {
          throw new CatalogWrappedRuntimeException("Unable to create database " + dbDefinition.name() + " as failed " +
            "to create its directory " + dbDefinition.locationUri(), e);
      }
      val dbModel = new DatabaseDesc();
      dbModel.db = dbDefinition;
      catalog.put(dbDefinition.name(), dbModel);
    }
  }

  @Override 
  public synchronized void dropDatabase(
      String db,
      boolean ignoreIfNotExists,
      boolean cascade) {
    if (catalog.contains(db)) {
    	DatabaseDesc databaseDesc = catalog.get(db).get();
      if (!cascade) {
        // If cascade is false, make sure the database is empty.
		if (databaseDesc.tables.nonEmpty()) {
          throw new CatalogRuntimeException("Database '" + db + "' is not empty. One or more tables exist.");
        }
        if (databaseDesc.functions.nonEmpty()) {
          throw new CatalogRuntimeException("Database '" + db + "' is not empty. One or more functions exist.");
        }
      }
      // Remove the database.
      val dbDefinition = databaseDesc.db;
      try {
        val location = new Path(dbDefinition.locationUri());
        val fs = location.getFileSystem(hadoopConfig);
        fs.delete(location, true);
      } catch (IOException e) {
          throw new CatalogWrappedRuntimeException("Unable to drop database " + dbDefinition.name() + " as failed " +
            "to delete its directory " + dbDefinition.locationUri(), e);
      }
      catalog.remove(db);
    } else {
      if (!ignoreIfNotExists) {
        throw new CatalogRuntimeException("No such database '" + db + "'");
      }
    }
  }

  @Override 
  public synchronized void alterDatabase(CatalogDatabase dbDefinition) {
	  DatabaseDesc dbDesc = getDb(dbDefinition.name());
	  dbDesc.db = dbDefinition;
  }

  @Override 
  public synchronized CatalogDatabase getDatabase(String db) {
	  DatabaseDesc dbDesc = getDb(db);
	  return dbDesc.db;
  }

  @Override 
  public synchronized boolean databaseExists(String db) {
    return catalog.contains(db);
  }

  @Override 
  public synchronized Seq<String> listDatabases() {
	  Set<String> databaseNames = catalog.keySet();
	  Seq<String> tmpres = databaseNames.toSeq();
//	  Ordering<String> comp = scala.math.Ordering.String$.MODULE$;
//	  Seq<String> sorted = tmpres.sorted(comp);
//	  return sorted;
	  return tmpres;
  }

  @Override 
  public synchronized Seq<String> listDatabases(String pattern) {
	  Seq<String> res = StringUtils.filterPattern(listDatabases(), pattern);
	  return res;
  }
  
  // --------------------------------------------------------------------------
  // Tables
  // --------------------------------------------------------------------------

//  @Override public createTable(
//      tableDefinition: CatalogTable,
//      ignoreIfExists: Boolean): Unit = synchronized {
//    assert(tableDefinition.identifier.database.isDefined)
//    val db = tableDefinition.identifier.database.get
//    requireDbExists(db)
//    val table = tableDefinition.identifier.table
//    if (tableExists(db, table)) {
//      if (!ignoreIfExists) {
//        throw new TableAlreadyExistsException(db = db, table = table)
//      }
//    } else {
//      // Set the default table location if this is a managed table and its location is not
//      // specified.
//      // Ideally we should not create a managed table with location, but Hive serde table can
//      // specify location for managed table. And in [[CreateDataSourceTableAsSelectCommand]] we have
//      // to create the table directory and write out data before we create this table, to avoid
//      // exposing a partial written table.
//      val needDefaultTableLocation =
//        tableDefinition.tableType == CatalogTableType.MANAGED &&
//          tableDefinition.storage.locationUri.isEmpty
//
//      val tableWithLocation = if (needDefaultTableLocation) {
//        val defaultTableLocation = new Path(new Path(catalog(db).db.locationUri), table)
//        try {
//          val fs = defaultTableLocation.getFileSystem(hadoopConfig)
//          fs.mkdirs(defaultTableLocation)
//        } catch {
//          case e: IOException =>
//            throw new SparkException(s"Unable to create table $table as failed " +
//              s"to create its directory $defaultTableLocation", e)
//        }
//        tableDefinition.withNewStorage(locationUri = Some(defaultTableLocation.toUri))
//      } else {
//        tableDefinition
//      }
//      val tableProp = tableWithLocation.properties.filter(_._1 != "comment")
//      catalog(db).tables.put(table, new TableDesc(tableWithLocation.copy(properties = tableProp)))
//    }
//  }
//
//  @Override public dropTable(
//      String db,
//      String table,
//      ignoreIfNotExists: Boolean,
//      purge: Boolean): Unit = synchronized {
//    requireDbExists(db)
//    if (tableExists(db, table)) {
//      val tableMeta = getTable(db, table)
//      if (tableMeta.tableType == CatalogTableType.MANAGED) {
//        // Delete the data/directory for each partition
//        val locationAllParts = catalog(db).tables(table).partitions.values.toSeq.map(_.location)
//        locationAllParts.foreach { loc =>
//          val partitionPath = new Path(loc)
//          try {
//            val fs = partitionPath.getFileSystem(hadoopConfig)
//            fs.delete(partitionPath, true)
//          } catch {
//            case e: IOException =>
//              throw new SparkException(s"Unable to delete partition path $partitionPath", e)
//          }
//        }
//        assert(tableMeta.storage.locationUri.isDefined,
//          "Managed table should always have table location, as we will assign a default location " +
//            "to it if it doesn't have one.")
//        // Delete the data/directory of the table
//        val dir = new Path(tableMeta.location)
//        try {
//          val fs = dir.getFileSystem(hadoopConfig)
//          fs.delete(dir, true)
//        } catch {
//          case e: IOException =>
//            throw new SparkException(s"Unable to drop table $table as failed " +
//              s"to delete its directory $dir", e)
//        }
//      }
//      catalog(db).tables.remove(table)
//    } else {
//      if (!ignoreIfNotExists) {
//        throw new NoSuchTableException(db = db, table = table)
//      }
//    }
//  }
//
//  @Override public renameTable(
//      String db,
//      oldName: String,
//      newName: String): Unit = synchronized {
//    requireTableExists(db, oldName)
//    requireTableNotExists(db, newName)
//    val oldDesc = catalog(db).tables(oldName)
//    oldDesc.table = oldDesc.table.copy(identifier = TableIdentifier(newName, Some(db)))
//
//    if (oldDesc.table.tableType == CatalogTableType.MANAGED) {
//      assert(oldDesc.table.storage.locationUri.isDefined,
//        "Managed table should always have table location, as we will assign a default location " +
//          "to it if it doesn't have one.")
//      val oldDir = new Path(oldDesc.table.location)
//      val newDir = new Path(new Path(catalog(db).db.locationUri), newName)
//      try {
//        val fs = oldDir.getFileSystem(hadoopConfig)
//        fs.rename(oldDir, newDir)
//      } catch {
//        case e: IOException =>
//          throw new SparkException(s"Unable to rename table $oldName to $newName as failed " +
//            s"to rename its directory $oldDir", e)
//      }
//      oldDesc.table = oldDesc.table.withNewStorage(locationUri = Some(newDir.toUri))
//    }
//
//    catalog(db).tables.put(newName, oldDesc)
//    catalog(db).tables.remove(oldName)
//  }
//
//  @Override public alterTable(tableDefinition: CatalogTable): Unit = synchronized {
//    assert(tableDefinition.identifier.database.isDefined)
//    val db = tableDefinition.identifier.database.get
//    requireTableExists(db, tableDefinition.identifier.table)
//    val updatedProperties = tableDefinition.properties.filter(kv => kv._1 != "comment")
//    val newTableDefinition = tableDefinition.copy(properties = updatedProperties)
//    catalog(db).tables(tableDefinition.identifier.table).table = newTableDefinition
//  }
//
//  @Override public alterTableDataSchema(
//      String db,
//      String table,
//      newDataSchema: StructType): Unit = synchronized {
//    requireTableExists(db, table)
//    val origTable = catalog(db).tables(table).table
//    val newSchema = StructType(newDataSchema ++ origTable.partitionSchema)
//    catalog(db).tables(table).table = origTable.copy(schema = newSchema)
//  }
//
//  @Override public alterTableStats(
//      String db,
//      String table,
//      stats: Option[CatalogStatistics]): Unit = synchronized {
//    requireTableExists(db, table)
//    val origTable = catalog(db).tables(table).table
//    catalog(db).tables(table).table = origTable.copy(stats = stats)
//  }
//
  @Override 
  public synchronized CatalogTable getTable(String db, String table) {
	  TableDesc t = tableOf(db, table);
	  return t.table;
  }

  @Override 
  public synchronized Seq<CatalogTable> getTablesByName(String db, Seq<String> tables) {
	  DatabaseDesc dbDesc = getDb(db);
	  // ??? tables.flatMap(dbDesc.tables.values()).map(_.table)
	  //?? tables.flatMap(SparkCatalogHelper.toScalaFunc(t -> dbDesc.tables.get(t)));
	  val res = new ArrayBuffer<CatalogTable>();
	  ScalaCollUtils.foreach(tables, n -> {
		  val tableOpt = dbDesc.tables.get(n);
		  if (tableOpt.isDefined()) {
			  res.$plus$eq(tableOpt.get().table);
		  }
	  });
	  return res;
  }

  	@Override 
  	public synchronized boolean tableExists(String db, String table) {
  		DatabaseDesc dbDesc = getDb(db);
  		return dbDesc.tables.contains(table);
  	}

  	@Override 
  	public synchronized Seq<String> listTables(String db) {
  		DatabaseDesc dbDesc = getDb(db);
  		Seq<String> tmpres = dbDesc.tables.keySet().toSeq();
//  		Ordering<String> comp = scala.math.Ordering.String$.MODULE$;
//  		Seq<String> res = tmpres.sorted(comp);
//  		return res;
  		return tmpres;
  	}

  @Override 
  public synchronized Seq<String> listTables(String db, String pattern) {
	  Seq<String> res = StringUtils.filterPattern(listTables(db), pattern);
	  return res;
  }

  @Override 
  public void loadTable(
      String db,
      String table,
      String loadPath,
      boolean isOverwrite,
      boolean  isSrcLocal) {
    throw new UnsupportedOperationException("loadTable is not implemented");
  }

  @Override 
  public void loadPartition(
      String db,
      String table,
      String loadPath,
      scala.collection.immutable.Map<String,String> partition,
      boolean isOverwrite,
      boolean inheritTableSpecs,
      boolean isSrcLocal) {
    throw new UnsupportedOperationException("loadPartition is not implemented.");
  }

  @Override 
  public void loadDynamicPartitions(
      String db,
      String table,
      String loadPath,
      scala.collection.immutable.Map<String,String> partition,
      boolean replace,
      int numDP) {
    throw new UnsupportedOperationException("loadDynamicPartitions is not implemented.");
  }

  // --------------------------------------------------------------------------
  // Partitions
  // --------------------------------------------------------------------------
		   
//  @Override 
//  public synchronized void createPartitions(
//      String db,
//      String table,
//      scala.collection.Seq<CatalogTablePartition> parts,
//      boolean ignoreIfExists) {
//    val tableDesc = tableOf(db, table);
//    val existingParts = tableDesc.partitions;
//    if (!ignoreIfExists) {
//    	// TODO
////    	scala.collection.Seq<TablePartitionSpec> dupSpecs = 
////    			parts.collect(SparkCatalogHelper.toScalaFunc((p) -> existingParts.contains(p.spec)? p.spec : null));
////    	if (dupSpecs.nonEmpty()) {
////    		throw new PartitionsAlreadyExistException(db, table, dupSpecs);
////    	}
//    }
//
//    CatalogTable catalogTable = tableDesc.table;
//	val partitionColumnNames = catalogTable.partitionColumnNames();
//    val tablePath = new Path(catalogTable.location());
//    // TODO: we should follow hive to roll back if one partition path failed to create.
//    parts.foreach(SparkCatalogHelper.toScalaFunc((p) -> {
//    	Path partitionPath = p.storage().locationUri().map(new Path(p)).getOrElse(() -> {
//			ExternalCatalogUtils.generatePartitionPath(p.spec, partitionColumnNames, tablePath);
//		});
//
//		try {
//			val fs = tablePath.getFileSystem(hadoopConfig);
//			if (!fs.exists(partitionPath)) {
//				fs.mkdirs(partitionPath);
//			}
//		} catch (IOException e) {
//			throw new SparkException("Unable to create partition path " + partitionPath, e);
//		}
//		CatalogStorageFormat st = p.storage();
//		CatalogStorageFormat storageCopy = new CatalogStorageFormat(
//				Option.apply(partitionPath.toUri()),
//			    st.inputFormat(),
//			    st.outputFormat(),
//			    st.serde(),
//			    st.compressed(),
//			    st.properties());
//		CatalogTablePartition partitionCopy = new CatalogTablePartition(
//      			p.spec(), storageCopy,
//      		    p.parameters(),
//      		    p.createTime(), p.lastAccessTime(),
//      		    p.stats());
//      	existingParts.put(p.spec(), partitionCopy);
//    }));
//  }
//
//  @Override 
//  public synchronized void dropPartitions(
//      String db,
//      String table,
//      Seq<scala.collection.immutable.Map<String,String>> partSpecs,
//      boolean ignoreIfNotExists,
//      boolean purge,
//      boolean retainData)  {
//	  val tableDesc = tableOf(db, table);
//	  val existingParts = tableDesc.partitions;
//    if (!ignoreIfNotExists) {
//    	Seq<scala.collection.immutable.Map<String,String>> missingSpecs = 
//    			partSpecs.collect(SparkCatalogHelper.toScalaFunc((s) -> !existingParts.contains(s)? s : null));
//    	if (missingSpecs.nonEmpty) {
//    		throw new NoSuchPartitionsException(db, table, missingSpecs);
//    	}
//    }
//
//    boolean shouldRemovePartitionLocation = (retainData)? 
//      false : tableDesc.tableType == CatalogTableType.MANAGED;
//
//    // TODO: we should follow hive to roll back if one partition path failed to delete, and support
//    // partial partition spec.
//    partSpecs.foreach(SparkCatalogHelper.toScalaFunc((s) -> {
//      if (existingParts.contains(p) && shouldRemovePartitionLocation) {
//        val partitionPath = new Path(existingParts(p).location);
//        try {
//          val fs = partitionPath.getFileSystem(hadoopConfig);
//          fs.delete(partitionPath, true);
//        } catch (IOException ex) {
//            throw new SparkException("Unable to delete partition path " + partitionPath, e);
//        }
//      }
//      existingParts.remove(p);
//    }));
//  }
//
//  @Override public synchronized void renamePartitions(
//      String db,
//      String table,
//      Seq<scala.collection.immutable.Map<String,String>> specs,
//      Seq<scala.collection.immutable.Map<String,String>> newSpecs) {
//    // require(specs.size == newSpecs.size, "number of old and new partition specs differ")
//	  val tableDesc = tableOf(db, table);
//    requirePartitionsExist(db, table, specs)
//    requirePartitionsNotExist(db, table, newSpecs)
//
//    val tableMeta = getTable(db, table)
//    val partitionColumnNames = tableMeta.partitionColumnNames
//    val tablePath = new Path(tableMeta.location)
//    val shouldUpdatePartitionLocation = getTable(db, table).tableType == CatalogTableType.MANAGED
//    val existingParts = catalog(db).tables(table).partitions
//    // TODO: we should follow hive to roll back if one partition path failed to rename.
//    specs.zip(newSpecs).foreach { case (oldSpec, newSpec) =>
//      val oldPartition = getPartition(db, table, oldSpec)
//      val newPartition = if (shouldUpdatePartitionLocation) {
//        val oldPartPath = new Path(oldPartition.location)
//        val newPartPath = ExternalCatalogUtils.generatePartitionPath(
//          newSpec, partitionColumnNames, tablePath)
//        try {
//          val fs = tablePath.getFileSystem(hadoopConfig)
//          fs.rename(oldPartPath, newPartPath)
//        } catch {
//          case e: IOException =>
//            throw new SparkException(s"Unable to rename partition path $oldPartPath", e)
//        }
//        oldPartition.copy(
//          spec = newSpec,
//          storage = oldPartition.storage.copy(locationUri = Some(newPartPath.toUri)))
//      } else {
//        oldPartition.copy(spec = newSpec)
//      }
//
//      existingParts.remove(oldSpec)
//      existingParts.put(newSpec, newPartition)
//    }
//  }
	
//	@Override public synchronized void alterPartitions(
//	      String db,
//	      String table,
//	      Seq<CatalogTablePartition> parts) {
//	    // requirePartitionsExist(db, table, parts.map(p => p.spec))
//		val tableDesc = tableOf(db, table);
//	    parts.foreach(SparkCatalogHelper.toScalaFuncV(p ->
//	    	tableDesc.partitions.put(p.spec, p)
//	    ));
//	  }

	@Override
	public synchronized CatalogTablePartition getPartition(String db, String table,
			scala.collection.immutable.Map<String, String> spec) {
		val tableDesc = tableOf(db, table);
		// TODO requirePartitionsExist(db, table, Seq(spec));
		CatalogTablePartition opt = tableDesc.partitions.get(spec).get();
		return opt;
	}

	@Override
	public synchronized Option<CatalogTablePartition> getPartitionOption(String db, String table,
			scala.collection.immutable.Map<String, String> spec) {
		val tableDesc = tableOf(db, table);
		if (!tableDesc.partitions.contains(spec)) {
			return Option.empty();
		} else {
			return tableDesc.partitions.get(spec);
		}
	}

//  @Override public synchronized Seq<String> listPartitionNames(
//      String db,
//      String table,
//      Option<scala.collection.immutable.Map<String,String>> partialSpec
//      ) {
//		val tableDesc = tableOf(db, table);
//		CatalogTable catalogTable = tableDesc.table;
//		val partitionColumnNames = catalogTable.partitionColumnNames();
//
//	    return listPartitions(db, table, partialSpec).map(SparkCatalogHelper.toScalaFunc((CatalogTablePartition partition) -> 
//	      partitionColumnNames.map(SparkCatalogHelper.toScalaFunc(name -> 
//	        escapePathName(name) + "=" + escapePathName(partition.spec(name))
//	      )).mkString("/")
//	    )).sorted();
//  }
//
//  @Override public synchronized Seq<CatalogTablePartition> listPartitions(
//      String db,
//      String table,
//      Option<scala.collection.immutable.Map<String,String>> partialSpec) {
//	  val tableDesc = tableOf(db, table);
//
//    if (partialSpec.isEmpty()) {
//      return tableDesc.partitions.values().toSeq();
//    } else {
//    	val partial = partialSpec.get();
//      	return tableDesc.partitions.toSeq().collect(SparkCatalogHelper.toScalaFunc(partition ->
//          isPartialPartitionSpec(partial, spec)? partition : null
//      			));
//      	}
//    }
//  }

	@Override
	public synchronized Seq<CatalogTablePartition> listPartitionsByFilter(String db, String table,
			Seq<org.apache.spark.sql.catalyst.expressions.Expression> predicates, String defaultTimeZoneId) {
		val tableDesc = tableOf(db, table);
		CatalogTable catalogTable = tableDesc.table;
		val allPartitions = tableDesc.partitions.values().toSeq();
		return ExternalCatalogUtils.prunePartitionsByFilter(catalogTable, allPartitions, predicates, defaultTimeZoneId);
	}

	// --------------------------------------------------------------------------
	// Functions
	// --------------------------------------------------------------------------

	@Override
	public synchronized void createFunction(String db, CatalogFunction func) {
		DatabaseDesc dbDesc = getDb(db);
		String funcName = func.identifier().funcName();
		val found = dbDesc.functions.get(funcName);
		if (found.isDefined()) {
			throw new CatalogRuntimeException("Function already exists '" + db + "." + funcName + "'");
		}
		dbDesc.functions.put(funcName, func);
	}

	@Override
	public synchronized void dropFunction(String db, String funcName) {
		DatabaseDesc dbModel = getDb(db);
		dbModel.requireFunctionExists(funcName);
		dbModel.functions.remove(funcName);
	}

	@Override
	public synchronized void alterFunction(String db, CatalogFunction func) {
		DatabaseDesc dbModel = getDb(db);
		String funcName = func.identifier().funcName();
		dbModel.requireFunctionExists(funcName);
		dbModel.functions.put(funcName, func);
	}

	@Override
	public synchronized void renameFunction(String db, String oldFuncName, String newFuncName) {
		DatabaseDesc dbModel = getDb(db);
		CatalogFunction oldFunc = dbModel.getFunction(oldFuncName);
		dbModel.requireFunctionNotExists(newFuncName);
		FunctionIdentifier newId = new FunctionIdentifier(newFuncName, Option.apply(db));
		val newFunc = new CatalogFunction(newId, oldFunc.className(), oldFunc.resources() // copy?
		);
		dbModel.functions.remove(oldFuncName);
		dbModel.functions.put(newFuncName, newFunc);
	}

	@Override
	public synchronized CatalogFunction getFunction(String db, String funcName) {
		DatabaseDesc dbModel = getDb(db);
		CatalogFunction func = dbModel.getFunction(funcName);
		return func;
	}

	@Override
	public synchronized boolean functionExists(String db, String funcName) {
		DatabaseDesc dbModel = getDb(db);
		return dbModel.functions.contains(funcName);
	}

	@Override
	public synchronized Seq<String> listFunctions(String db, String pattern) {
		DatabaseDesc dbModel = getDb(db);
		Seq<String> funcNames = dbModel.functions.keysIterator().toSeq();
		return StringUtils.filterPattern(funcNames, pattern);
	}

}
