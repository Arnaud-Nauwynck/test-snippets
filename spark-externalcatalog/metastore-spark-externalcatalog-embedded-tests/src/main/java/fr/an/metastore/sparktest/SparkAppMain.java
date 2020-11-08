package fr.an.metastore.sparktest;

import static fr.an.metastore.spark.util.ScalaCollUtils.seqAsJavaList;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalog.Catalog;
import org.apache.spark.sql.catalog.Database;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.ExternalCatalogWithListener;
import org.apache.spark.sql.connector.catalog.CatalogManager;
import org.apache.spark.sql.connector.catalog.CatalogPlugin;
import org.apache.spark.sql.connector.catalog.Identifier;
import org.apache.spark.sql.connector.catalog.Table;

import fr.an.metastore.spark.EmbeddedExternalCatalog;
import lombok.extern.slf4j.Slf4j;
import scala.collection.Seq;

@Slf4j
public class SparkAppMain {
	SparkSession spark;
	SparkContext sc;
	JavaSparkContext jsc;
	Configuration hadoopConf;
	Catalog catalog;
	ExternalCatalogWithListener externalCatalog; // => Hive only!
	EmbeddedExternalCatalog embeddedExternalCatalog;
	
	public static void main(String[] args) {
		try {
			SparkAppMain app = new SparkAppMain();
			app.parseArgs(args);
			app.run();
			
			System.out.println("Finished");
		} catch(Exception ex) {
			System.err.println("Failed");
			ex.printStackTrace();
		}
	}

	private void parseArgs(String[] args) {
//		for(int i = 0; i < args.length; i++) {
//			String a = args[i];
//			
//		}
	}

	public  void run() throws Exception {
		SparkConf sparkConfig = new SparkConf();
		// see used in CatalogManager
		sparkConfig.set("spark.sql.catalog.spark_catalog", 
				EmbeddedExternalCatalog.class.getName());
		// sparkConfig.set("spark.sql.catalog.spark_catalog.type", "hive");  // ??
				
//		sparkConfig.set(EmbeddedExternalCatalog.CONFIG_FILE_PROPERTY, 
//				EmbeddedExternalCatalog.DEFAULT_CONFIG_FILENAME);
		
		this.spark = SparkSession.builder()
				.appName("test-spark-external-catalog-embedded")
				.master("local[*]")
				// .enableHiveSupport()
				.config(sparkConfig)
				.getOrCreate();
		this.sc = spark.sparkContext();
		this.jsc = new JavaSparkContext(sc);
		this.hadoopConf = sc.hadoopConfiguration();
		this.catalog  = spark.catalog();

		CatalogManager catalogManager = spark.sessionState().catalogManager();
		CatalogPlugin currentCatalog = catalogManager.currentCatalog();
		log.info("spark.sessionState().catalogManager().currentCatalog(): " + currentCatalog);

		if (currentCatalog instanceof EmbeddedExternalCatalog) {
			embeddedExternalCatalog = (EmbeddedExternalCatalog) currentCatalog;
		}

		checkTmpHiveDirAndPerms();
		this.externalCatalog = spark.sharedState().externalCatalog();
		// lazy loading externalCatalog =>
//		org.apache.spark.sql.AnalysisException: java.lang.RuntimeException: The root scratch dir: /tmp/hive on HDFS should be writable. Current permissions are: ---------;
//		at org.apache.spark.sql.hive.HiveExternalCatalog.withClient(HiveExternalCatalog.scala:109)
//		at org.apache.spark.sql.hive.HiveExternalCatalog.databaseExists(HiveExternalCatalog.scala:221)
//		at org.apache.spark.sql.internal.SharedState.externalCatalog$lzycompute(SharedState.scala:154)
//		at org.apache.spark.sql.internal.SharedState.externalCatalog(SharedState.scala:144)

		try {
			runInSpark();
		} finally {
			spark.close(); // .stop(); ??
			this.spark = null;
			this.jsc = null;
		}
	}

	private void checkTmpHiveDirAndPerms() throws IOException {
		Path tmpHivePath = new Path("/tmp/hive");
		FileSystem tmpFs = FileSystem.get(tmpHivePath.toUri(), hadoopConf);
		if (! tmpFs.exists(tmpHivePath)) {
			log.warn("/tmp/hive does not exists? .. creating");
			tmpFs.mkdirs(tmpHivePath);
		}
		FileStatus tmpHiveFileStatus = tmpFs.getFileStatus(tmpHivePath);
		FsPermission tmpHivePerms = tmpHiveFileStatus.getPermission();
		if (!tmpHivePerms.getUserAction().implies(FsAction.ALL)
				|| !tmpHivePerms.getGroupAction().implies(FsAction.ALL)
				|| !tmpHivePerms.getOtherAction().implies(FsAction.ALL)
				) {
			log.warn("missing read-write perm to " + tmpHivePath);
			FsPermission expectedHivePerms = new FsPermission(
					FsAction.ALL, FsAction.ALL, FsAction.ALL);
			tmpFs.setPermission(tmpHivePath, expectedHivePerms);

			// recheck
			FileStatus checkTmpHiveFileStatus = tmpFs.getFileStatus(tmpHivePath);
			FsPermission checkTmpHivePerms = checkTmpHiveFileStatus.getPermission();
			if (!checkTmpHivePerms.getUserAction().implies(FsAction.READ_WRITE)
					|| !checkTmpHivePerms.getGroupAction().implies(FsAction.READ_WRITE)
					|| !checkTmpHivePerms.getOtherAction().implies(FsAction.READ_WRITE)
					) {
				log.error("same missing perms after set!");
			}
		}
	}

	private void runInSpark() throws Exception {
		System.out.println();
		
		// externalCatalog => NOT using catalogV2 !!! => use Hive thrift client..
		// cf code: SharedState
        //  lazy val externalCatalog: ExternalCatalogWithListener = {
        //   val externalCatalog = SharedState.reflect[ExternalCatalog, SparkConf, Configuration](
        //     SharedState.externalCatalogClassName(conf), conf, hadoopConf)
        //  ..
        //   val wrapped = new ExternalCatalogWithListener(externalCatalog)
        // 
        //  private val HIVE_EXTERNAL_CATALOG_CLASS_NAME = "org.apache.spark.sql.hive.HiveExternalCatalog"
        //
        //  private def externalCatalogClassName(conf: SparkConf): String = {
        //    conf.get(CATALOG_IMPLEMENTATION) match {
        //      case "hive" => HIVE_EXTERNAL_CATALOG_CLASS_NAME
        //      case "in-memory" => classOf[InMemoryCatalog].getCanonicalName
        //    }
        //  }
        //
		boolean showHiveExternalCatalog = false;
		if (showHiveExternalCatalog) {
			log.info("externalCatalog.listDatabases()");
			Seq<String> scalaListDatabases = this.externalCatalog.listDatabases();
			List<String> listDatabaseNames = seqAsJavaList(scalaListDatabases);
			// ONLY hive dbs: 
			// => [ "defaut" ]
			log.info("=> " + listDatabaseNames); 
		}

		{ // catalog => delegate also to externalCatalog !!!
			log.info("spark.catalog.listDatabases()");
			Dataset<Database> dsDatabases = catalog.listDatabases();
			dsDatabases.show(false);
			// => 
			// +-------+----------------+---------------------------------------------------------------------------------------------------------------------------------+
			// |name   |description     |locationUri                                                                                                                      |
			// +-------+----------------+---------------------------------------------------------------------------------------------------------------------------------+
			// |default|default database|file:/D:/arn/devPerso/mygithub/test-snippets/spark-externalcatalog/metastore-spark-externalcatalog-embedded-tests/spark-warehouse|
			// +-------+----------------+---------------------------------------------------------------------------------------------------------------------------------+

			List<Database> listDatabases = dsDatabases.collectAsList();
			boolean debugRedo = true;
			if (debugRedo) {
				listDatabases = catalog.listDatabases().collectAsList();
			}
			log.info("=> " + listDatabases); 
		}
		
		CatalogManager catalogManager = spark.sessionState().catalogManager();
		CatalogPlugin currentCatalog = catalogManager.currentCatalog();
		log.info("spark.sessionState().catalogManager().currentCatalog(): " + currentCatalog);

		{
			Seq<String> listDatabases = embeddedExternalCatalog.listDatabases();
			log.info("currentCatalog listDatabases():" + seqAsJavaList(listDatabases));
			// => [ "db1" ]
		}

		{
			log.info("spark.sql('show databases')");
			spark.sql("show databases").show(false);
			// => 
			// +---------+
			// |namespace|
			// +---------+
			// |db1      |
			// +---------+
		}
		
		log.info("spark.sql('SHOW TABLES IN db1')");
		spark.sql("SHOW TABLES IN db1").show(false);
		// => 
		// +---------+---------+
		// |namespace|tableName|
		// +---------+---------+
		// |db1      |table1   |
		// +---------+---------+

		
		log.info("spark.sql('SHOW TABLES FROM db1')");
		spark.sql("SHOW TABLES FROM db1").show(false);
		// => 
		// +---------+---------+
		// |namespace|tableName|
		// +---------+---------+
		// |db1      |table1   |
		// +---------+---------+
		// 
		
		log.info("spark.sql('DESCRIBE TABLE db1.table1')");
		spark.sql("DESCRIBE TABLE db1.table1").show(false);
		// => 
		// +----------------+---------+-------+
		// |col_name        |data_type|comment|
		// +----------------+---------+-------+
		// |strField        |string   |       |
		// |strNullableField|string   |       |
		// |intField        |int      |       |
		// |intNullableField|int      |       |
		// |                |         |       |
		// |# Partitioning  |         |       |
		// |Not partitioned |         |       |
		// +----------------+---------+-------+

//		try {
//			log.info("spark.sql('DESCRIBE EXTENDED TABLE db1.table1') .... FAILS?!");
//			spark.sql("DESCRIBE EXTENDED TABLE db1.table1").show(false);
//		} catch(Exception ex) {
//			// Failed
//			// org.apache.spark.sql.AnalysisException: Describing columns is not supported for v2 tables.;
//			// 	at org.apache.spark.sql.catalyst.analysis.ResolveCatalogs$$anonfun$apply$1.applyOrElse(ResolveCatalogs.scala:122)
//			// 	at org.apache.spark.sql.catalyst.analysis.ResolveCatalogs$$anonfun$apply$1.applyOrElse(ResolveCatalogs.scala:34)
//			// 	at org.apache.spark.sql.catalyst.plans.logical.AnalysisHelper.$anonfun$resolveOperatorsDown$2(AnalysisHelper.scala:108)
//		}
		
		{ // get table V1
			log.info("catalog.getTable(db1, table1)  .. as V1");
			CatalogTable table1 = embeddedExternalCatalog.getTable("db1", "table1");
			log.info("CatalogTable:" + table1);
			log.info("table1.location" + table1.location());
			// =>
			// Database: db1
			// Table: table1
			// Created Time: Thu Jan 01 01:00:00 CET 1970
			// Last Access: UNKNOWN
			// Created By: Spark 
			// Type: EXTERNAL
			// Location: file:///src/test/data/db1/table1
			// Serde Library: AVRO
			// Schema: root
			//  |-- strField: string (nullable = false)
			//  |-- strNullableField: string (nullable = true)
			//  |-- intField: integer (nullable = false)
			//  |-- intNullableField: integer (nullable = true)
			// )
		}
		
		{ // get table V2
			Identifier identifier = Identifier.of(new String[] { "db1" }, "table1");
			Table table1 = embeddedExternalCatalog.loadTable(identifier);
			log.info("Table:" + table1);
			log.info("table1.properties('path')" + table1.properties().get("path"));
		}
		

	}

}
