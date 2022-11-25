package fr.an.tests.testspark;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;

public class SimpleMain {

	public static void main(String[] args) {
		System.out.println("ensure HADOOP_HOME env var is set, (and Winutils on windows)");
		System.out.println("HADOOP_HOME: " + System.getenv("HADOOP_HOME"));

		System.out.println("start embedded SparkContext - SparkSession");
		SparkSession spark = SparkSession.builder()
				.master("local[*]")
				.appName("myapp")	
				.getOrCreate();
		
		List<String> ls = Arrays.asList("Hello spark");
		Dataset<String> ds = spark.createDataset(ls, Encoders.STRING());
		ds.show();
		
		System.out.println("finish SparkContext");
		spark.close();
	}
}
