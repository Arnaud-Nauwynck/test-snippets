package fr.an.tests.testsparkdeltalake;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;

import com.google.common.collect.ImmutableMap;

import io.delta.tables.DeltaTable;
import lombok.val;
import scala.Tuple2;

public class AppMain {

	SparkSession spark;
	JavaSparkContext jsc;
	
	String basicFilename = "basic";
	String inputFilename;
	
	public static void main(String[] args) {
		try {
			AppMain app = new AppMain();
			app.parseArgs(args);
			app.run();
			
			System.out.println("Finished");
		} catch(Exception ex) {
			System.err.println("Failed");
			ex.printStackTrace();
		}
	}

	private void parseArgs(String[] args) {
		for(int i = 0; i < args.length; i++) {
			String a = args[i];
			if (a.equals("--inputFile")) {
				this.inputFilename = args[++i];
			} else {
				throw new IllegalArgumentException("Unrecognized arg '" + a + "'");
			}
			
		}
		
	}

	public  void run() throws Exception {
		this.spark = SparkSession.builder()
				.appName("test-spark-deltalake")
				.master("local[1]")
//				.enableHiveSupport()
				.config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
			    .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
			    
			    .config("spark.databricks.delta.retentionDurationCheck.enabled", "false") // for vacuum 0 ... 
				//	Failed
				//	java.lang.IllegalArgumentException: requirement failed: Are you sure you would like to vacuum files with such a low retention period? If you have
				//	writers that are currently writing to this table, there is a risk that you may corrupt the
				//	state of your Delta table.
				//	
				//	If you are certain that there are no operations being performed on this table, such as
				//	insert/upsert/delete/optimize, then you may turn off this check by setting:
				//	spark.databricks.delta.retentionDurationCheck.enabled = false
				//	
				//	If you are not sure, please use a value not less than "168 hours".
			    
				.getOrCreate();
		this.jsc = new JavaSparkContext(spark.sparkContext());
		try {
			runInSpark();
		} finally {
			spark.close(); // .stop(); ??
			this.spark = null;
			this.jsc = null;
		}
	}

	private void runInSpark() throws Exception {
		runBasic();
//		runInputFile();
	}
	
	private void runBasic() throws Exception {
		if (basicFilename == null) {
			basicFilename = "basic";
		}
		File basicFile = new File(basicFilename);
		
		// Cleanup previous data if any
		if (basicFile.exists()) {
			System.out.println("cleanup: delete dir '" + basicFile + "'");
			FileUtils.deleteDirectory(basicFile);
		}
		
		String path = basicFile.getCanonicalPath();
		{
		    val data = spark.range(0, 5).coalesce(1);
		    data.write().format("delta").save(path);
		}
		
	    // Read table
		{
			System.out.println("Reading the table");
		    val df = spark.read().format("delta").load(path);
		    df.show();
		}
		
		DeltaTable deltaTable = DeltaTable.forPath(path);
		
		System.out.println("Show DeltaTable DF");
		deltaTable.toDF().show(2, false);
		
		// Upsert (merge) new data
		{
		    System.out.println("Upsert new data");
		    Dataset<Row> newData = spark.range(0, 20).coalesce(1).toDF();
	
		    deltaTable.as("oldData")
		      .merge(
		        newData.as("newData"),
		        "oldData.id = newData.id")
		      .whenMatched()
		      .updateExpr(ImmutableMap.of("id", "newData.id"))
		      // equivalent to ?? .update(ImmutableMap.of("id", newData.col("id")))
		      .whenNotMatched()
		      .insertExpr(ImmutableMap.of("id", "newData.id"))
		      // equivalent to?? .insert(ImmutableMap.of("id", newData.col("id")))
		      .execute();
	
		    deltaTable.toDF().show();
		}
		
	    // Update table data
		{
			System.out.println("Overwrite the table");
		    val data = spark.range(5, 10).coalesce(1);
		    data.write().format("delta").mode("overwrite").save(path);
		    deltaTable.toDF().show();
		}
		
	    // Update every even value by adding 100 to it
		{
			System.out.println("Update to the table (add 100 to every even value)");
			deltaTable.update(
					functions.expr("id % 2 == 0"),
					ImmutableMap.of("id", functions.expr("id + 100")));
			deltaTable.toDF().show();
		}
		
	    // Delete every even value
		{
			System.out.println("Update to the table (delete every even value)");
		    deltaTable.delete(functions.expr("id % 2 == 0"));
		    deltaTable.toDF().show();
		}
		
		// Read the data
		{
			deltaTable.toDF().show();
		}
		
	    // Read old version of the data using time travel
		{
		    System.out.print("Read old data using time travel");
		    val df2 = spark.read().format("delta").option("versionAsOf", 0).load(path);
		    df2.show();
		}
		
		// Vacuum
		{
			System.out.println("vacuum");
			deltaTable.vacuum(0.0);
			deltaTable.toDF().show();
		}
		

	}
	
	private void runInputFile() {
		Dataset<String> inputFileDs = spark.read().textFile(inputFilename);
		JavaRDD<String> jinputFileDs = inputFileDs.javaRDD();
		JavaRDD<Tuple2<String, Integer>> colsDs = jinputFileDs.map(line -> {
			String[] cols = line.split(";");
			String colStr = cols[0];
			int colInt = Integer.parseInt(cols[1]);
			return new Tuple2<>(colStr, colInt);
		});
	
		// inputFileDs.write()
//		colsDs.  write ??
		// spark.write().format("delta");
	}
}
