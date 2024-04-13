package fr.an.tests.testsparkrepartition;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SparkAppMain {

	private String appName = "SparkApp";
	private String master = "spark://localhost:7077";

	SparkSession spark;

	SparkContext sc; // = spark.sparkContext();
	JavaSparkContext jsc; // = JavaSparkContext.fromSparkContext(sc);
	Configuration hadoopConf; // = jsc.hadoopConfiguration();
	FileSystem hadoopFs; // = FileSystem.get(hadoopConf);

	
	public static void main(String[] args) {
		SparkAppMain app = new SparkAppMain();
		try {
			app.run();
			
			System.out.println("Finished");
		} catch(Exception ex) {
			System.err.println("Failed, exiting");
			ex.printStackTrace(System.err);
		}
	}

	private void run() throws Exception {
		System.out.println("ensure HADOOP_HOME env var is set, (and Winutils on windows)");
		System.out.println("HADOOP_HOME: " + System.getenv("HADOOP_HOME"));

		SparkConf sparkConf = new SparkConf()
				.setAppName(appName)
				.setMaster(master)
				;
		
		this.spark = SparkSession.builder()
					.config(sparkConf)
					.getOrCreate();
		try {
			this.sc = spark.sparkContext();
			this.jsc = JavaSparkContext.fromSparkContext(sc);
			this.hadoopConf = jsc.hadoopConfiguration();
			this.hadoopFs = FileSystem.get(hadoopConf);
			
			// do not generate the "_SUCCESS" files
			this.hadoopConf.set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false");
			// do not generate the ".crc" files (only on LocalFileSystem)
			hadoopFs.setWriteChecksum(false);

			DebugUDF.registerUDFs(spark.udf());


			doRunApp();
		
		} finally {
			spark.stop();
		}
	}


	// --------------------------------------------------------------------------------------------

	@Data
	@AllArgsConstructor
	public static class MyBean {
		public String _1;
		public int _2;
	}

	private void doRunApp() throws Exception {
		sanityCheckHelloSpark();

		checkInitializeDb();

		Dataset<MyBean> ds = createDataset_MyBeans(100_000);

		// runPartitionTest_all(ds);
		runPartitionTest2_byPartitionedColumn(ds);


		BufferedReader stdinReader = new BufferedReader(new InputStreamReader(System.in));
		repl_loop: for(;;) {
			System.out.println("prompt to interactive re-execute? ");
			System.out.println("[0] run all test: 1,2,..5 ");
			System.out.println("[1] test .repartition(25).write..");
			System.out.println("[2] test .repartition($'_2').write..  (correct partitioned column for table)");
			System.out.println("[3] test .repartition($'_1').write..  (WRONG partitioned column for table)");
			System.out.println("[4] test AQE disable .repartition($'_1').write..  (WRONG partitioned column for table, and AQE disabled)");
			System.out.println("[5] .repartition($'_2', callUDF('udfDebugIdentityInt', $'_2')).write..");
			System.out.println("[q] to exit");
			System.out.println("Enter your choice to re-execute: ");
			String line = stdinReader.readLine();
			if (line == null) {
				continue;
			}
			line = line.trim();
			switch(line) {
				case "0":
					runPartitionTest_all(ds);
					break;

				case "1":
					runPartitionTest1_byCount(ds);
					break;

				case "2":
					runPartitionTest2_byPartitionedColumn(ds);
					break;

				case "3":
					runPartitionTest3_byWrongColumn(ds);
					break;

				case "4":
					runPartitionTest4_byWrongColumnAndAQEDisabled(ds);
					break;

				case "5":
					runPartitionTest5_byPartitionedColumnAndUDF(ds);
					break;

				case "q":
					break repl_loop;
			}
		}

	}

	private void runPartitionTest_all(Dataset<MyBean> ds) {
		runPartitionTest1_byCount(ds);

		runPartitionTest2_byPartitionedColumn(ds);

		runPartitionTest3_byWrongColumn(ds);

		runPartitionTest4_byWrongColumnAndAQEDisabled(ds);

		runPartitionTest5_byPartitionedColumnAndUDF(ds);
	}

	private void sanityCheckHelloSpark() {
		List<String> ls = Arrays.asList("Hello spark");
		Dataset<String> helloWorldDs = spark.createDataset(ls, Encoders.STRING());
		helloWorldDs.show();
	}

	private void checkInitializeDb() {
		spark.sql("create database if not exists db1").show();
		spark.sql("show tables in db1").show();
	}

	private Dataset<MyBean> createDataset_MyBeans(int count) {
		List<MyBean> beans = new ArrayList<>();
		for(int i = 0; i < count; i++) {
			beans.add(new MyBean("" + i, i % 100));
		}
		Dataset<MyBean> ds = spark.createDataset(beans, Encoders.bean(MyBean.class));
		return ds;
	}

	private void runPartitionTest1_byCount(Dataset<MyBean> ds) {
		System.out.println("INSERT TEST 1 ... repartition(25).insert");

		sqlWithCallSite("drop table if exists db1.test_part1");
		sqlWithCallSite("create table db1.test_part1 (col1 string) partitioned by (part1 int) stored as parquet");

		withCallSite(".repartition(25).write..", () -> {
			ds.repartition(25).write().mode("overwrite").insertInto("db1.test_part1");
		});
	}


	private void runPartitionTest2_byPartitionedColumn(Dataset<MyBean> ds) {
		System.out.println("INSERT TEST 2 ... repartition($'_2').insert");

		sqlWithCallSite("drop table if exists db1.test_part2");
		sqlWithCallSite("create table db1.test_part2 (col1 string) partitioned by (part1 int) stored as parquet");

		withCallSite(".repartition($'_2').write..", () -> {
			ds.repartition(ds.col("_2")).write().mode("overwrite").insertInto("db1.test_part2");
		});
	}

	private void runPartitionTest3_byWrongColumn(Dataset<MyBean> ds) {
		System.out.println("INSERT TEST 3 ... repartition($'_1').insert");

		sqlWithCallSite("drop table if exists db1.test_part3");
		sqlWithCallSite("create table db1.test_part3 (col1 string) partitioned by (part1 int) stored as parquet");

		withCallSite(".repartition($'_2').write..", () -> {
			ds.repartition(ds.col("_1")).write().mode("overwrite").insertInto("db1.test_part3");
		});
	}

	private void runPartitionTest4_byWrongColumnAndAQEDisabled(Dataset<MyBean> ds) {
		sc.setCallSite("INSERT TEST_PART4  AQE disabled, repartition($'_1').insert");

		spark.sql("drop table if exists db1.test_part4");
		spark.sql("create table db1.test_part4 (col1 string) partitioned by (part1 int) stored as parquet");

		withCallSite("AQE disabled, .repartition($'_2').write..", () -> {
			spark.conf().set("spark.sql.adaptive.enabled", "false");
			ds.repartition(ds.col("_1")).write().mode("overwrite").insertInto("db1.test_part4");
			spark.conf().set("spark.sql.adaptive.enabled", "true");
		});
	}

	private void runPartitionTest5_byPartitionedColumnAndUDF(Dataset<MyBean> ds) {
		System.out.println("INSERT TEST 5 ... repartition($'_2', functions.callUDF('udfDebugIdentityInt', col('_2')).insert");

		sqlWithCallSite("drop table if exists db1.test_part5");
		sqlWithCallSite("create table db1.test_part5 (col1 string) partitioned by (part1 int) stored as parquet");

		withCallSite(".repartition($'_5', callUDF('udfDebugIdentityInt', $'_2')).write..", () -> {
			ds.repartition(ds.col("_2"), functions.callUDF(DebugUDF.NAME_udfDebugIdentityInt, ds.col("_2")))
					.write().mode("overwrite").insertInto("db1.test_part2");
		});
	}


	private void sqlWithCallSite(String sql) {
		withCallSite("SQL " + sql, () -> {
			spark.sql(sql).show();
		});
	}

	private void withCallSite(String shortMessage, Runnable action) {
		sc.setCallSite(shortMessage);
		try {
			action.run();
		} finally {
			sc.clearCallSite();
		}
	}

}
