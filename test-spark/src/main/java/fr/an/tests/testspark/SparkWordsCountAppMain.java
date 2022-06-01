package fr.an.tests.testspark;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import scala.Serializable;
import scala.collection.JavaConverters;
import scala.collection.TraversableOnce;
import scala.runtime.AbstractFunction1;

@Slf4j
public class SparkWordsCountAppMain {
	
	public static void main(String[] args) {
		// use -Xmx >= 500m  .. otherwise... java.lang.IllegalArgumentException: System memory 259522560 must be at least 471859200. Please increase heap size using the --driver-memory option or spark.driver.memory in Spark configuration.
		SparkConf sparkConf = new SparkConf()
				.setAppName("Spark Words Count")
				.setMaster("local[*]");
		SparkSession spark = SparkSession.builder()
					.config(sparkConf)
					.getOrCreate();
		try {
			Dataset<String> loremIpsum = spark.read().textFile("data/loremIpsum.txt");
			
			log.info("show() truncated");
			loremIpsum.show();
			
			long lineCount = loremIpsum.count();
			log.info("line count:" + lineCount);
			
			Dataset<String> wordDs = loremIpsum.flatMap((FlatMapFunction<String,String>) line -> {
					String[] res = line.replace("[,.?!]", " ").replace("  ", " ").split(" ");
					return Arrays.asList(res).iterator();
				} , Encoders.STRING());
			wordDs.cache();
			
			log.info("words show() truncated");
			wordDs.show();
			
			long wordCount = wordDs.count();
			log.info("words count:" + wordCount);
			
			log.info("finished");
		} finally {
			spark.stop();
		}

//		testSparkScalaApi(loremIpsum);
	}

	@Data
	public static class PojoBean {
		long id;
		String col1;
		String col2;
	}
	@Data @AllArgsConstructor
	public static class PojoOutputBean {
		long id;
		String col1;
		String col2;
		String computed;
	}
	private static PojoOutputBean computePojo2Output(PojoBean src) {
		return new PojoOutputBean(src.id, src.col1, src.col2, src.col1 + " - " + src.col2);
	}
	
	private static void advancedSparkSqlCode(SparkSession spark) {
		Dataset<Row> ds = spark.sql(
				"SELECT latest.id, latest.col1, latest.col2, joined.col3"
				+ " FROM ( SELECT *"
				+ "   , row_number() OVER (PARTITION BY id ORDER BY timestamp DESC) as rankNum"
				+ "   FROM some_hive_db.input_table"
				+ "   WHERE rankNum=1"
				+ "   ) latest"
				+ " LEFT JOIN department joined ON latest.deptno = joined.deptno"
				)
			// => convert Dataset<Row> -> Dataset<PojoBean>
			.as(Encoders.bean(PojoBean.class))
			// => compute enrich -> Dataset<PojoOutputBean>
			.map((MapFunction<PojoBean,PojoOutputBean>) (pojo -> computePojo2Output(pojo)), Encoders.bean(PojoOutputBean.class))
			// => convert -> Dataset<Row>
			.toDF();
		
		// optimize for read later
		ds // .repartition(1) .. equivalent to coalesce()
			// .orderBy("id")
			.repartition(2, ds.col("col1"))
			.sortWithinPartitions("id")
			// => write as PARQUET
			.write()
			.format("hive").mode(SaveMode.Overwrite)
			.insertInto("some_hive_db.output_table");
	}




	private static class LineToWordFunc extends AbstractFunction1<String,TraversableOnce<String>> implements Serializable {
		private static final long serialVersionUID = 1L;
		@Override
		public TraversableOnce<String> apply(String line) {
			String[] res = line.replace("[,.?!]", " ").replace("  ", " ").split(" ");
			return JavaConverters.iterableAsScalaIterableConverter(Arrays.asList(res)).asScala();
		}
	}

	private static void testSparkScalaApi(Dataset<String> loremIpsum) {
		scala.Function1<String,TraversableOnce<String>> lineToWordScalaFunc = new LineToWordFunc(); // serializable sub-class
		Dataset<String> wordScalaDs = loremIpsum.flatMap(lineToWordScalaFunc, Encoders.STRING());
		wordScalaDs.show();
	}
	
	
}
