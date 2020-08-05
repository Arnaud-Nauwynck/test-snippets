package fr.an.spark.externalcatalog;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
// import org.apache.spark.sql.catalyst.catalog.CatalogTypes.scala.collection.mutable.Map<String,String>; type scala.collection.mutable.Map<String,String> = Map[String, String] 
import org.apache.spark.sql.catalyst.analysis.TableAlreadyExistsException;
import org.apache.spark.sql.catalyst.catalog.CatalogDatabase;
import org.apache.spark.sql.catalyst.catalog.CatalogFunction;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;
import org.apache.spark.sql.catalyst.catalog.ExternalCatalog;
import org.apache.spark.sql.catalyst.util.StringUtils;

import fr.an.spark.externalcatalog.impl.SparkCatalogHelper;
import lombok.val;
import scala.Option;
import scala.collection.Seq;
import scala.collection.mutable.ArrayBuffer;
import scala.math.Ordering;

public abstract class InMemoryCatalog implements ExternalCatalog {
	
	private SparkConf conf;
	
	private Configuration hadoopConfig;
	
	public static class CatalogRuntimeException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public CatalogRuntimeException(String message) {
			super(message);
		}
		
	}

	public static class CatalogWrappedRuntimeException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public CatalogWrappedRuntimeException(String message, Exception ex) {
			super(message, ex);
		}
		
	}
	

	
  private static class TableDesc {
	  CatalogTable table;
	  scala.collection.mutable.HashMap<scala.collection.mutable.Map<String,String>, CatalogTablePartition> partitions = 
			  new scala.collection.mutable.HashMap<scala.collection.mutable.Map<String,String>, CatalogTablePartition>();
  }

  // @RequiredArgsConstructor
  private static class DatabaseDesc {
	  CatalogDatabase db;
	  scala.collection.mutable.HashMap<String, TableDesc> tables = new scala.collection.mutable.HashMap<String, TableDesc>();
	  scala.collection.mutable.HashMap<String, CatalogFunction> functions = new scala.collection.mutable.HashMap<String, CatalogFunction>();

	  public DatabaseDesc(CatalogDatabase db) {
		  this.db = db;
	  }
  }

  // Database name -> description
  private scala.collection.mutable.HashMap<String, DatabaseDesc> catalog = new scala.collection.mutable.HashMap<String, DatabaseDesc>();

  private boolean partitionExists(String db, String table, 
		  scala.collection.mutable.Map<String,String> spec) {
    requireTableExists(db, table);
    DatabaseDesc dbDesc = catalog.get(db).get();
	TableDesc tableDesc = dbDesc.tables.get(table).get();
	return tableDesc.partitions.contains(spec);
  }

  private DatabaseDesc getDb(String db) {
	  Option<DatabaseDesc> opt = catalog.get(db);
	  if (opt.isEmpty()) {
		  throw new CatalogRuntimeException("NNo such database '" + db + "'");
	  }
	  return opt.get();
  }

  private TableDesc tableOf(String db, String table) {
	  DatabaseDesc dbDesc = getDb(db);
	  Option<TableDesc> opt = dbDesc.tables.get(table);
	  if (opt.isEmpty()) {
		  throw new CatalogRuntimeException("No such table '" + db + "." + table + "'");
	  }
	  return opt.get();
  }
  
  private void requireTableNotExists(String db, String table) throws TableAlreadyExistsException {
		if (tableExists(db, table)) {
			throw new TableAlreadyExistsException(db, table);
		}
  }

  private void requirePartitionsExist(
      String db,
      String table,
      Seq<scala.collection.mutable.Map<String,String>> specs
      ) {
	  SparkCatalogHelper.foreach(specs, s -> {
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
      Seq<scala.collection.mutable.Map<String,String>> specs) {
	  SparkCatalogHelper.foreach(specs, s -> {
		  if (partitionExists(db, table, s)) {
	    	  throw new RuntimeException(
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
    	  throw new RuntimeException("Database already exists '" + dbDefinition.name() + "'");
        // throw new DatabaseAlreadyExistsException(dbDefinition.name());
      }
    } else {
      try {
        val location = new Path(dbDefinition.locationUri());
        val fs = location.getFileSystem(hadoopConfig);
        fs.mkdirs(location);
      } catch (IOException e) {
          throw new RuntimeException("Unable to create database " + dbDefinition.name() + " as failed " +
            "to create its directory " + dbDefinition.locationUri(), e);
      }
      catalog.put(dbDefinition.name(), new DatabaseDesc(dbDefinition));
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
	  Seq<String> tmpres = catalog.keySet().toSeq();
	  Ordering<String> comp = scala.math.Ordering.String$.MODULE$;
	  Seq<String> sorted = tmpres.sorted(comp);
	  return sorted;
  }

  @Override 
  public synchronized Seq<String> listDatabases(String pattern) {
	  Seq<String> res = StringUtils.filterPattern(listDatabases(), pattern);
	  return res;
  }
  
//  // --------------------------------------------------------------------------
//  // Tables
//  // --------------------------------------------------------------------------
//
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
	  val res = new ArrayBuffer<CatalogTable>();
	  SparkCatalogHelper.foreach(tables, n -> {
		  val tableOpt = dbDesc.tables.get(n);
		  if (tableOpt.isDefined()) {
			  res.$plus$eq(tableOpt.get().table);
		  }
	  });
	  return res;
  }

//  public scala.collection.Seq<org.apache.spark.sql.catalyst.catalog.CatalogTable> getTablesByName(java.lang.String, scala.collection.Seq<java.lang.String>);
//  Code:
//     0: aload_0
//     1: aload_1
//     2: invokevirtual #262                // Method requireDbExists:(Ljava/lang/String;)V
//     5: aload_2
//     6: aload_0
//     7: invokespecial #89                 // Method catalog:()Lscala/collection/mutable/HashMap;
//    10: aload_1
//    11: invokevirtual #95                 // Method scala/collection/mutable/HashMap.apply:(Ljava/lang/Object;)Ljava/lang/Object;
//    14: checkcast     #23                 // class org/apache/spark/sql/catalyst/catalog/InMemoryCatalog$DatabaseDesc
//    17: invokevirtual #98                 // Method org/apache/spark/sql/catalyst/catalog/InMemoryCatalog$DatabaseDesc.tables:()Lscala/collection/mutable/HashMap;
//    20: astore_3
//    21: aload_3
//    22: invokedynamic #689,  0            // InvokeDynamic #8:apply:(Lscala/collection/mutable/HashMap;)Lscala/Function1;
//    27: getstatic     #553                // Field scala/collection/Seq$.MODULE$:Lscala/collection/Seq$;
//    30: invokevirtual #557                // Method scala/collection/Seq$.canBuildFrom:()Lscala/collection/generic/CanBuildFrom;
//    33: invokeinterface #692,  3          // InterfaceMethod scala/collection/Seq.flatMap:(Lscala/Function1;Lscala/collection/generic/CanBuildFrom;)Ljava/lang/Object;
//    38: checkcast     #694                // class scala/collection/TraversableLike
//    41: invokedynamic #701,  0            // InvokeDynamic #9:apply:()Lscala/Function1;
//    46: getstatic     #553                // Field scala/collection/Seq$.MODULE$:Lscala/collection/Seq$;
//    49: invokevirtual #557                // Method scala/collection/Seq$.canBuildFrom:()Lscala/collection/generic/CanBuildFrom;
//    52: invokeinterface #702,  3          // InterfaceMethod scala/collection/TraversableLike.map:(Lscala/Function1;Lscala/collection/generic/CanBuildFrom;)Ljava/lang/Object;
//    57: checkcast     #144                // class scala/collection/Seq
//    60: areturn

    

  	@Override 
  	public synchronized boolean tableExists(String db, String table) {
  		DatabaseDesc dbDesc = getDb(db);
  		return dbDesc.tables.contains(table);
  	}

  	@Override 
  	public synchronized Seq<String> listTables(String db) {
  		DatabaseDesc dbDesc = getDb(db);
  		Ordering<String> comp = scala.math.Ordering.String$.MODULE$;
  		Seq<String> res = dbDesc.tables.keySet().toSeq().sorted(comp);
  		return res;
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

//  public synchronized void createPartitions(java.lang.String, java.lang.String, scala.collection.Seq<org.apache.spark.sql.catalyst.catalog.CatalogTablePartition>, boolean);
//  Code:
//     0: aload_0
//     1: aload_1
//     2: aload_2
//     3: invokevirtual #87                 // Method requireTableExists:(Ljava/lang/String;Ljava/lang/String;)V
//     6: aload_0
//     7: invokespecial #89                 // Method catalog:()Lscala/collection/mutable/HashMap;
//    10: aload_1
//    11: invokevirtual #95                 // Method scala/collection/mutable/HashMap.apply:(Ljava/lang/Object;)Ljava/lang/Object;
//    14: checkcast     #23                 // class org/apache/spark/sql/catalyst/catalog/InMemoryCatalog$DatabaseDesc
//    17: invokevirtual #98                 // Method org/apache/spark/sql/catalyst/catalog/InMemoryCatalog$DatabaseDesc.tables:()Lscala/collection/mutable/HashMap;
//    20: aload_2
//    21: invokevirtual #95                 // Method scala/collection/mutable/HashMap.apply:(Ljava/lang/Object;)Ljava/lang/Object;
//    24: checkcast     #26                 // class org/apache/spark/sql/catalyst/catalog/InMemoryCatalog$TableDesc
//    27: invokevirtual #101                // Method org/apache/spark/sql/catalyst/catalog/InMemoryCatalog$TableDesc.partitions:()Lscala/collection/mutable/HashMap;
//    30: astore        5
//    32: iload         4
//    34: ifne          89
//    37: aload_3
//    38: new           #17                 // class org/apache/spark/sql/catalyst/catalog/InMemoryCatalog$$anonfun$1
//    41: dup
//    42: aconst_null
//    43: aload         5
//    45: invokespecial #739                // Method org/apache/spark/sql/catalyst/catalog/InMemoryCatalog$$anonfun$1."<init>":(Lorg/apache/spark/sql/catalyst/catalog/InMemoryCatalog;Lscala/collection/mutable/HashMap;)V
//    48: getstatic     #553                // Field scala/collection/Seq$.MODULE$:Lscala/collection/Seq$;
//    51: invokevirtual #557                // Method scala/collection/Seq$.canBuildFrom:()Lscala/collection/generic/CanBuildFrom;
//    54: invokeinterface #743,  3          // InterfaceMethod scala/collection/Seq.collect:(Lscala/PartialFunction;Lscala/collection/generic/CanBuildFrom;)Ljava/lang/Object;
//    59: checkcast     #144                // class scala/collection/Seq
//    62: astore        6
//    64: aload         6
//    66: invokeinterface #744,  1          // InterfaceMethod scala/collection/Seq.nonEmpty:()Z
//    71: ifeq          86
//    74: new           #746                // class org/apache/spark/sql/catalyst/analysis/PartitionsAlreadyExistException
//    77: dup
//    78: aload_1
//    79: aload_2
//    80: aload         6
//    82: invokespecial #748                // Method org/apache/spark/sql/catalyst/analysis/PartitionsAlreadyExistException."<init>":(Ljava/lang/String;Ljava/lang/String;Lscala/collection/Seq;)V
//    85: athrow
//    86: goto          89
//    89: aload_0
//    90: aload_1
//    91: aload_2
//    92: invokevirtual #534                // Method getTable:(Ljava/lang/String;Ljava/lang/String;)Lorg/apache/spark/sql/catalyst/catalog/CatalogTable;
//    95: astore        7
//    97: aload         7
//    99: invokevirtual #751                // Method org/apache/spark/sql/catalyst/catalog/CatalogTable.partitionColumnNames:()Lscala/collection/Seq;
//   102: astore        8
//   104: new           #370                // class org/apache/hadoop/fs/Path
//   107: dup
//   108: aload         7
//   110: invokevirtual #586                // Method org/apache/spark/sql/catalyst/catalog/CatalogTable.location:()Ljava/net/URI;
//   113: invokespecial #376                // Method org/apache/hadoop/fs/Path."<init>":(Ljava/net/URI;)V
//   116: astore        9
//   118: aload_3
//   119: aload_0
//   120: aload         8
//   122: aload         9
//   124: aload         5
//   126: invokedynamic #761,  0            // InvokeDynamic #10:apply:(Lorg/apache/spark/sql/catalyst/catalog/InMemoryCatalog;Lscala/collection/Seq;Lorg/apache/hadoop/fs/Path;Lscala/collection/mutable/HashMap;)Lscala/Function1;
//   131: invokeinterface #148,  2          // InterfaceMethod scala/collection/Seq.foreach:(Lscala/Function1;)V
//   136: return

		   
  @Override 
  public synchronized void createPartitions(
      String db,
      String table,
      scala.collection.Seq<CatalogTablePartition> parts,
      boolean ignoreIfExists) {
    val tableDesc = tableOf(db, table);
    val existingParts = tableDesc.partitions;
//    if (!ignoreIfExists) {
//      val dupSpecs = parts.collect { case p if existingParts.contains(p.spec) => p.spec }
//      if (dupSpecs.nonEmpty) {
//        throw new PartitionsAlreadyExistException(db = db, table = table, specs = dupSpecs)
//      }
//    }
//
//    val tableMeta = getTable(db, table)
//    val partitionColumnNames = tableMeta.partitionColumnNames
//    val tablePath = new Path(tableMeta.location)
//    // TODO: we should follow hive to roll back if one partition path failed to create.
//    parts.foreach { p =>
//      val partitionPath = p.storage.locationUri.map(new Path(_)).getOrElse {
//        ExternalCatalogUtils.generatePartitionPath(p.spec, partitionColumnNames, tablePath)
//      }
//
//      try {
//        val fs = tablePath.getFileSystem(hadoopConfig)
//        if (!fs.exists(partitionPath)) {
//          fs.mkdirs(partitionPath)
//        }
//      } catch {
//        case e: IOException =>
//          throw new SparkException(s"Unable to create partition path $partitionPath", e)
//      }
//
//      existingParts.put(
//        p.spec,
//        p.copy(storage = p.storage.copy(locationUri = Some(partitionPath.toUri))))
//    }
  }

//  @Override public dropPartitions(
//      String db,
//      String table,
//      partSpecs: Seq[scala.collection.mutable.Map<String,String>],
//      ignoreIfNotExists: Boolean,
//      purge: Boolean,
//      retainData: Boolean): Unit = synchronized {
//    requireTableExists(db, table)
//    val existingParts = catalog(db).tables(table).partitions
//    if (!ignoreIfNotExists) {
//      val missingSpecs = partSpecs.collect { case s if !existingParts.contains(s) => s }
//      if (missingSpecs.nonEmpty) {
//        throw new NoSuchPartitionsException(db = db, table = table, specs = missingSpecs)
//      }
//    }
//
//    val shouldRemovePartitionLocation = if (retainData) {
//      false
//    } else {
//      getTable(db, table).tableType == CatalogTableType.MANAGED
//    }
//
//    // TODO: we should follow hive to roll back if one partition path failed to delete, and support
//    // partial partition spec.
//    partSpecs.foreach { p =>
//      if (existingParts.contains(p) && shouldRemovePartitionLocation) {
//        val partitionPath = new Path(existingParts(p).location)
//        try {
//          val fs = partitionPath.getFileSystem(hadoopConfig)
//          fs.delete(partitionPath, true)
//        } catch {
//          case e: IOException =>
//            throw new SparkException(s"Unable to delete partition path $partitionPath", e)
//        }
//      }
//      existingParts.remove(p)
//    }
//  }
//
//  @Override public renamePartitions(
//      String db,
//      String table,
//      specs: Seq[scala.collection.mutable.Map<String,String>],
//      newSpecs: Seq[scala.collection.mutable.Map<String,String>]): Unit = synchronized {
//    require(specs.size == newSpecs.size, "number of old and new partition specs differ")
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
//
//  @Override public alterPartitions(
//      String db,
//      String table,
//      parts: Seq[CatalogTablePartition]): Unit = synchronized {
//    requirePartitionsExist(db, table, parts.map(p => p.spec))
//    parts.foreach { p =>
//      catalog(db).tables(table).partitions.put(p.spec, p)
//    }
//  }
//
//  @Override public getPartition(
//      String db,
//      String table,
//      spec: scala.collection.mutable.Map<String,String>): CatalogTablePartition = synchronized {
//    requirePartitionsExist(db, table, Seq(spec))
//    catalog(db).tables(table).partitions(spec)
//  }
//
//  @Override public getPartitionOption(
//      String db,
//      String table,
//      spec: scala.collection.mutable.Map<String,String>): Option[CatalogTablePartition] = synchronized {
//    if (!partitionExists(db, table, spec)) {
//      None
//    } else {
//      Option(catalog(db).tables(table).partitions(spec))
//    }
//  }
//
//  @Override public listPartitionNames(
//      String db,
//      String table,
//      partialSpec: Option[scala.collection.mutable.Map<String,String>] = None): Seq[String] = synchronized {
//    val partitionColumnNames = getTable(db, table).partitionColumnNames
//
//    listPartitions(db, table, partialSpec).map { partition =>
//      partitionColumnNames.map { name =>
//        escapePathName(name) + "=" + escapePathName(partition.spec(name))
//      }.mkString("/")
//    }.sorted
//  }
//
//  @Override public listPartitions(
//      String db,
//      String table,
//      partialSpec: Option[scala.collection.mutable.Map<String,String>] = None): Seq[CatalogTablePartition] = synchronized {
//    requireTableExists(db, table)
//
//    partialSpec match {
//      case None => catalog(db).tables(table).partitions.values.toSeq
//      case Some(partial) =>
//        catalog(db).tables(table).partitions.toSeq.collect {
//          case (spec, partition) if isPartialPartitionSpec(partial, spec) => partition
//        }
//    }
//  }
//
//  @Override public listPartitionsByFilter(
//      String db,
//      String table,
//      predicates: Seq[Expression],
//      defaultTimeZoneId: String): Seq[CatalogTablePartition] = {
//    val catalogTable = getTable(db, table)
//    val allPartitions = listPartitions(db, table)
//    prunePartitionsByFilter(catalogTable, allPartitions, predicates, defaultTimeZoneId)
//  }
//
//  // --------------------------------------------------------------------------
//  // Functions
//  // --------------------------------------------------------------------------
//
//  @Override public createFunction(String db, func: CatalogFunction): Unit = synchronized {
//    requireDbExists(db)
//    requireFunctionNotExists(db, func.identifier.funcName)
//    catalog(db).functions.put(func.identifier.funcName, func)
//  }
//
//  @Override public dropFunction(String db, funcName: String): Unit = synchronized {
//    requireFunctionExists(db, funcName)
//    catalog(db).functions.remove(funcName)
//  }
//
//  @Override public alterFunction(String db, func: CatalogFunction): Unit = synchronized {
//    requireDbExists(db)
//    requireFunctionExists(db, func.identifier.funcName)
//    catalog(db).functions.put(func.identifier.funcName, func)
//  }
//
//  @Override public renameFunction(
//      String db,
//      oldName: String,
//      newName: String): Unit = synchronized {
//    requireFunctionExists(db, oldName)
//    requireFunctionNotExists(db, newName)
//    val newFunc = getFunction(db, oldName).copy(identifier = FunctionIdentifier(newName, Some(db)))
//    catalog(db).functions.remove(oldName)
//    catalog(db).functions.put(newName, newFunc)
//  }
//
//  @Override public getFunction(String db, funcName: String): CatalogFunction = synchronized {
//    requireFunctionExists(db, funcName)
//    catalog(db).functions(funcName)
//  }
//
//  @Override public functionExists(String db, funcName: String): Boolean = synchronized {
//    requireDbExists(db)
//    catalog(db).functions.contains(funcName)
//  }
//
//  @Override public listFunctions(String db, pattern: String): Seq[String] = synchronized {
//    requireDbExists(db)
//    StringUtils.filterPattern(catalog(db).functions.keysIterator.toSeq, pattern)
//  }

}
