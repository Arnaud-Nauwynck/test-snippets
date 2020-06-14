

set SPARK_SCALA_VERSION=2.12

set JAVA_HOME=C:\apps\jvm\jdk1.8.0_202
set PATH=%PATH%;%JAVA_HOME%\bin

set HADOOP_CONF_DIR=D:\arn\hadoop\conf-nohdfs
set HADOOP_HOME=D:\arn\hadoop\hadoop-3.2.1


// Reading csv from "file://" (no infered schema)
val filesDS = spark.read.option("delimiter", ";").option("header", true).csv("file:///d:/arn/hadoop/rootfs/filesWithHeader.csv")
filesDS.describe()
filesDS.printSchema()

root
 |-- perms: string (nullable = true)
 |-- a: string (nullable = true)
 |-- user: string (nullable = true)
 |-- group: string (nullable = true)
 |-- c2: string (nullable = true)
 |-- c3: string (nullable = true)
 |-- c4: string (nullable = true)
 |-- size: string (nullable = true)
 |-- month: string (nullable = true)
 |-- day: string (nullable = true)
 |-- hour: string (nullable = true)
 |-- filename: string (nullable = true)


val filesDS = spark.read.option("delimiter", ";").option("header", true).option("inferSchema", "true").csv("file:///d:/arn/hadoop/rootfs/filesWithHeader.csv")
filesDS.printSchema()

root
 |-- perms: string (nullable = true)
 |-- a: integer (nullable = true)
 |-- user: string (nullable = true)
 |-- group: string (nullable = true)
 |-- c2: integer (nullable = true)
 |-- c3: string (nullable = true)
 |-- c4: string (nullable = true)
 |-- size: string (nullable = true)
 |-- month: string (nullable = true)
 |-- day: string (nullable = true)
 |-- hour: string (nullable = true)
 |-- filename: string (nullable = true)


filesDS.select("filename", "size", "perms").take(20)

val filesDS = spark.read.option("delimiter", ";").option("header", true).option("inferSchema", "true").csv("file:///d:/arn/hadoop/rootfs/filesWithHeader.csv");
filesDS.write.parquet("file:///d:/arn/hadoop/rootfs/files.parquet");
 
2019-12-08 15:42:26,109 INFO datasources.SQLHadoopMapReduceCommitProtocol: Using output committer class org.apache.parquet.hadoop.ParquetOutputCommitter
java.io.IOException: Cannot run program "D:\arn\hadoop\hadoop-3.2.1\bin\winutils.exe": CreateProcess error=216, Cette version de %1 nÆest pas compatible avec la version de Windows actuellement exÚcutÚe. VÚrifiez dans les informations systÞme de votre ordinateur, puis contactez lÆÚditeur de logiciel
  at java.lang.ProcessBuilder.start(ProcessBuilder.java:1048)
  at org.apache.hadoop.util.Shell.runCommand(Shell.java:935)
  at org.apache.hadoop.util.Shell.run(Shell.java:901)
  at org.apache.hadoop.util.Shell$ShellCommandExecutor.execute(Shell.java:1213)
  at org.apache.hadoop.util.Shell.execCommand(Shell.java:1307)
  at org.apache.hadoop.util.Shell.execCommand(Shell.java:1289)
  at org.apache.hadoop.fs.RawLocalFileSystem.setPermission(RawLocalFileSystem.java:865)
  


filesDS.write.parquet("/files.parquet");
filesDS.write.mode('overwrite').parquet("/files.parquet")
 
import org.apache.hadoop.fs.{FileSystem, FileUtil, Path}
val fs = FileSystem.get( sc.hadoopConfiguration )
fs.listStatus(new Path("/")).foreach(st => println(st.getPath()))

/dir1
/files
/files.csv
/filesWithHeader.csv

spark-version-info.properties
set SPARK_SCALA_VERSION=2.12

set SPARK_SUBMIT_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000



val filesDS = spark.read.option("delimiter", ";").option("header", true).option("inferSchema", "true").csv("fs1:///filesWithHeader.csv");
// filesDS.write.mode("overwrite").parquet("fs1:///files.parquet");

val files2DS = spark.read.parquet("fs1:///files.parquet");

// add extra column computed from hash .. in [0, 10(
val filesWithHashed = files2DS.withColumn("hashed", abs(hash($"filename")) % 10)

// write with partitions..
filesWithHashed.write.partitionBy("hashed").parquet("fs1:///dir1/files")



// check read partitions
val checkPartionned = spark.read.parquet("fs1:///dir1/files")
 
//
checkPartionned.filter("hashed=3").count()


checkPartionned.filter("hashed=3").explain(true)
==>
2019-12-08 18:24:17,504 INFO v2.DataSourceV2Strategy:
Pushing operators to parquet fs1:/dir1/files
Pushed Filters: isnotnull(hashed#482), (hashed#482 = 3)
Post-Scan Filters: isnotnull(hashed#482),(hashed#482 = 3)
Output: hashed#482
... NO predicate PushDown !!!!

== Parsed Logical Plan ==
'Filter ('hashed = 3)
+- RelationV2[perms#470, a#471, user#472, group#473, c2#474, c3#475, c4#476, size#477, month#478, day#479, hour#480, filename#481, hashed#482] parquet fs1:/dir1/files

== Analyzed Logical Plan ==
perms: string, a: int, user: string, group: string, c2: int, c3: string, c4: string, size: string, month: string, day: string, hour: string, filename: string, hashed: int
Filter (hashed#482 = 3)
+- RelationV2[perms#470, a#471, user#472, group#473, c2#474, c3#475, c4#476, size#477, month#478, day#479, hour#480, filename#481, hashed#482] parquet fs1:/dir1/files

== Optimized Logical Plan ==
Filter (isnotnull(hashed#482) AND (hashed#482 = 3))
+- RelationV2[perms#470, a#471, user#472, group#473, c2#474, c3#475, c4#476, size#477, month#478, day#479, hour#480, filename#481, hashed#482] parquet fs1:/dir1/files

== Physical Plan ==
*(1) Project [perms#470, a#471, user#472, group#473, c2#474, c3#475, c4#476, size#477, month#478, day#479, hour#480, filename#481, hashed#482]
+- *(1) Filter (isnotnull(hashed#482) AND (hashed#482 = 3))
   +- *(1) ColumnarToRow
      +- BatchScan[perms#470, a#471, user#472, group#473, c2#474, c3#475, c4#476, size#477, month#478, day#479, hour#480, filename#481, hashed#482] ParquetScan Location: InMemoryFileIndex[fs1:/dir1/files], ReadSchema: struct<perms:string,a:int,user:string,group:string,c2:int,c3:string,c4:string,size:string,month:s...


checkPartionned.filter(col("hashed").equalTo(3)).count()

checkPartionned.filter(col("hashed").equalTo(3)).explain()
== Physical Plan ==
*(1) Project [perms#470, a#471, user#472, group#473, c2#474, c3#475, c4#476, size#477, month#478, day#479, hour#480, filename#481, hashed#482]
+- *(1) Filter (isnotnull(hashed#482) AND (hashed#482 = 3))
   +- *(1) ColumnarToRow
      +- BatchScan[perms#470, a#471, user#472, group#473, c2#474, c3#475, c4#476, size#477, month#478, day#479, hour#480, filename#481, hashed#482] ParquetScan Location: InMemoryFileIndex[fs1:/dir1/files], ReadSchema: struct<perms:string,a:int,user:string,group:string,c2:int,c3:string,c4:string,size:string,month:s...
	  


checkPartionned.filter(col("hashed") === 3).explain()

	  
val fileSchema = checkPartionned.filter(col("hashed").equalTo(3)).schema
fileSchema: org.apache.spark.sql.types.StructType = StructType(StructField(perms,StringType,true), StructField(a,IntegerType,true), StructField(user,StringType,true), StructField(group,StringType,true), StructField(c2,IntegerType,true), StructField(c3,StringType,true), StructField(c4,StringType,true), StructField(size,StringType,true), StructField(month,StringType,true), StructField(day,StringType,true), StructField(hour,StringType,true), StructField(filename,StringType,true), StructField(hashed,IntegerType,true))


checkPartionned.createOrReplaceTempView("files")
checkPartionned.createOrReplaceGlobalTempView("files");

scala> spark.sql("select * from files").show()
2019-12-08 18:42:45,483 INFO hive.HiveUtils: Initializing HiveMetastoreConnection version 2.3.6 using Spark classes.
2019-12-08 18:42:45,829 INFO conf.HiveConf: Found configuration file null
2019-12-08 18:42:46,550 INFO session.SessionState: Created HDFS directory: /tmp/hive/arnaud
org.apache.spark.sql.AnalysisException: java.lang.RuntimeException: java.io.IOException: Cannot run program "D:\arn\hadoop\hadoop-3.2.1\bin\winutils.exe": CreateProcess error=216, Cette version de %1 nÆest pas compatible avec la version de Windows actuellement exÚcutÚe. VÚrifiez dans les informations systÞme de votre ordinateur, puis contactez lÆÚditeur de logiciel;
  at org.apache.spark.sql.hive.HiveExternalCatalog.withClient(HiveExternalCatalog.scala:109)
  at org.apache.spark.sql.hive.HiveExternalCatalog.databaseExists(HiveExternalCatalog.scala:221)
  at org.apache.spark.sql.internal.SharedState.externalCatalog$lzycompute(SharedState.scala:147)
  at org.apache.spark.sql.internal.SharedState.externalCatalog(SharedState.scala:137)
  at org.apache.spark.sql.internal.SharedState.globalTempViewManager$lzycompute(SharedState.scala:170)	  
  
  
// trying to write as table ...
filesWithHashed.write.format("parquet").partitionBy("hashed").option("path", "fs1:///dir1/files2").saveAsTable("files")

2019-12-08 19:45:57,463 INFO session.SessionState: Created HDFS directory: /tmp/hive/arnaud/ef22e0f6-a44f-4a94-b410-b94d6fd6c312
org.apache.spark.sql.AnalysisException: java.lang.RuntimeException: java.io.IOException: Cannot run program "D:\arn\hadoop\hadoop-3.2.1\bin\winutils.exe": CreateProcess error=216, Cette version de %1 nÆest pas compatible avec la version de Windows actuellement exÚcutÚe. VÚrifiez dans les informations systÞme de votre ordinateur, puis contactez lÆÚditeur de logiciel;
  at org.apache.spark.sql.hive.HiveExternalCatalog.withClient(HiveExternalCatalog.scala:109)
  at org.apache.spark.sql.hive.HiveExternalCatalog.databaseExists(HiveExternalCatalog.scala:221)
  at org.apache.spark.sql.internal.SharedState.externalCatalog$lzycompute(SharedState.scala:147)
  at org.apache.spark.sql.internal.SharedState.externalCatalog(SharedState.scala:137)
  at org.apache.spark.sql.hive.HiveSessionStateBuilder.externalCatalog(HiveSessionStateBuilder.scala:40)
  at org.apache.spark.sql.hive.HiveSessionStateBuilder.$anonfun$catalog$1(HiveSessionStateBuilder.scala:55)
  at org.apache.spark.sql.catalyst.catalog.SessionCatalog.externalCatalog$lzycompute(SessionCatalog.scala:91
    
  