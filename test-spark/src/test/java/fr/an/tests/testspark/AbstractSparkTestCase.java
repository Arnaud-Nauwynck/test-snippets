package fr.an.tests.testspark;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

public abstract class AbstractSparkTestCase {

	protected static SparkSession spark;
	protected static SparkContext sc; // = spark.sparkContext();
	protected static JavaSparkContext jsc; // = JavaSparkContext.fromSparkContext(sc);
	protected static Configuration hadoopConf; // = jsc.hadoopConfiguration();
	protected static FileSystem hadoopFs; // = FileSystem.get(hadoopConf);

	protected static boolean skipWriteChecksumFiles = true;
	
	protected static void startSparkSession() {
		if (null != spark) {
			return; //already started
		}
		
		SparkConf sparkConf = new SparkConf()
				.setAppName("test-spark")
				.setMaster("local[*]")
				;
		
		spark = SparkSession.builder()
					.config(sparkConf)
					.getOrCreate();
		sc = spark.sparkContext();
		jsc = JavaSparkContext.fromSparkContext(sc);
		hadoopConf = jsc.hadoopConfiguration();
		try {
			hadoopFs = FileSystem.get(hadoopConf);
		} catch (IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
		
		// do not generate the "_SUCCESS" files..
		hadoopConf.set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false");

		// do not generate the ".crc" files.. (only on LocalFileSystem??)
		if (skipWriteChecksumFiles) {
			// hadoopFs.setVerifyChecksum(false);  ... when disabled ... Failed!!
			hadoopFs.setWriteChecksum(false);
		}
			
	}

	protected static void stopSparkSession() {
		if (spark != null) {
			spark.stop();
			spark = null;
		}
	}
	
}
