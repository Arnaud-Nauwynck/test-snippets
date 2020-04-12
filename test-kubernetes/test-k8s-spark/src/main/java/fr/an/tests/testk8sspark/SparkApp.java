package fr.an.tests.testk8sspark;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class SparkApp {

	public static void main(String[] args) {
		new SparkApp().run();
	}

	private void run() {

		SparkSession spark = SparkSession.builder().appName("TestSpark")
				.getOrCreate();
		System.out.println("created SparkSession");
		try {
			JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

			List<String[]> stringAsList = new ArrayList<>();
			stringAsList.add(new String[] { "bar1.1", "bar2.1" });
			stringAsList.add(new String[] { "bar1.2", "bar2.2" });

			JavaRDD<Row> rowRDD = jsc.parallelize(stringAsList).map((String[] row) -> RowFactory.create(row));

			// Creates schema
			StructType schema = DataTypes
					.createStructType(new StructField[] { DataTypes.createStructField("foe1", DataTypes.StringType, false),
							DataTypes.createStructField("foe2", DataTypes.StringType, false) });

			Dataset<Row> df = spark.sqlContext().createDataFrame(rowRDD, schema).toDF();

			long count = df.count();
			System.out.println("df.count() => " + count);

			System.out.println("sleeping before finish...");
			sleep(3 * 60_000);
			System.out.println("finishing...");

		} finally {
			spark.close();
		}

	}

	private void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}

}