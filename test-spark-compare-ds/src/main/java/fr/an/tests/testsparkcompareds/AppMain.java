package fr.an.tests.testsparkcompareds;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Column;

import scala.Tuple2;

public class AppMain {

	SparkSession spark;
	JavaSparkContext jsc;
	
	private String leftFile = "src/test/data/file1.csv";
	private String rightFile = "src/test/data/file2.csv";
	
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
			if (a.equals("--left")) {
				this.leftFile = args[++i];
			} else if (a.equals("--right")) {
				this.rightFile = args[++i];
			} else {
				throw new IllegalArgumentException("Unrecognized arg '" + a + "'");
			}
		}
	}

	public  void run() throws Exception {
		this.spark = SparkSession.builder()
				.appName("test-spark-xml")
				.master("local[1]")
//				.enableHiveSupport()
			    
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
		Dataset<Row> leftDs = spark.read().option("header",true).csv(leftFile);
		Dataset<Row> rightDs = spark.read().option("header",true).csv(rightFile);
		leftDs.persist();
		rightDs.persist();
		try {
			System.out.println("Left DS:");
			leftDs.show();
			
			System.out.println("Right DS:");
			rightDs.show();
			
//			compareDs_byId(spark, leftDs, rightDs);
//			compareDs_values(spark, leftDs, rightDs);
			compareDs_joinWith(spark, leftDs, rightDs);
			
		} finally {
			leftDs.unpersist();
			rightDs.unpersist();
		}
		
		
	}

	private static void compareDs_byId(SparkSession spark, 
		Dataset<Row> leftDs, Dataset<Row> rightDs) {

		JavaPairRDD<String, Row> leftByIdRDD = leftDs.toJavaRDD().mapToPair(new PairFunction<Row, String, Row>() {
			private static final long serialVersionUID = 1L;
			public Tuple2<String, Row> call(Row row) {
				String id = row.getString(0);
				return new Tuple2<String, Row>(id, row);
			}
		});
		JavaPairRDD<String, Row> rightByIdRDD = rightDs.toJavaRDD().mapToPair(new PairFunction<Row, String, Row>() {
			private static final long serialVersionUID = 1L;
			public Tuple2<String, Row> call(Row row) {
				String id = row.getString(0);
				return new Tuple2<String, Row>(id, row);
			}
		});
		
		JavaPairRDD<String, Row> leftOnlyByIdRDD = leftByIdRDD.subtractByKey(rightByIdRDD);
		JavaRDD<Row> leftOnlyRDD = leftOnlyByIdRDD.values();
		Dataset<Row> leftOnlyDs = spark.createDataFrame(leftOnlyRDD, leftDs.schema());
		System.out.println("Left Only DS");
		leftOnlyDs.show();
		System.out.println(".. done left only");
		
		JavaPairRDD<String, Row> rightOnlyByIdRDD = rightByIdRDD.subtractByKey(leftByIdRDD);
		JavaRDD<Row> rightOnlyRDD = rightOnlyByIdRDD.values();
		Dataset<Row> rightOnlyDs = spark.createDataFrame(rightOnlyRDD, rightDs.schema());
		System.out.println("Right Only DS");
		rightOnlyDs.show();
		System.out.println(".. done right only");
	}
	
	private static void compareDs_values(SparkSession spark, 
			Dataset<Row> leftDs, Dataset<Row> rightDs) {
		Dataset<Row> leftOnlyOrDiffer = leftDs.except(rightDs);
		leftOnlyOrDiffer.show();
		System.out.println(".. left only (by values) or differ");

		Dataset<Row> rightOnlyOrDiffer = rightDs.except(leftDs);
		rightOnlyOrDiffer.show();
		System.out.println(".. right only (by values) or differ");

	}

	private static void compareDs_joinWith(SparkSession spark, 
			Dataset<Row> leftDs, Dataset<Row> rightDs) {
		System.out.println("diffDS = leftDs.joinWithColumn(rightDs, leftDs.col('id') === rightDs.col('id'), 'fullouter')");
		Dataset<Tuple2<Row, Row>> joinRes = leftDs.joinWith(rightDs, 
				leftDs.col("id").$eq$eq$eq(rightDs.col("id")), "fullouter");
		joinRes.show();
		
		System.out.println("Diff");
		System.out.println("joinDs.filter( not( joinDs.col('_1') ==== joinDs.col('_2') ) )");
		Dataset<Tuple2<Row, Row>> diffDS = joinRes.filter(
				org.apache.spark.sql.functions.not(
						joinRes.col("_1").$eq$eq$eq(joinRes.col("_2")
								)
						)
				);
		diffDS.persist();
		try {
			diffDS.show();	
			System.out.println();
			
			System.out.println("Left Only");
			System.out.println("joinDs.filter(isnull(joinDs.col('_2')) .select(joinDs.col('_1'))");
			Dataset<Row> leftOnlyDS = joinRes.filter(
					org.apache.spark.sql.functions.isnull(joinRes.col("_2"))
					).select(joinRes.col("_1"));
			leftOnlyDS.show();
			System.out.println();
			
			System.out.println("Right Only");
			System.out.println("joinDs.filter(isnull(joinDs.col('_1')) .select(joinDs.col('_2'))");
			Dataset<Row> rightOnlyDS = joinRes.filter(
					org.apache.spark.sql.functions.isnull(joinRes.col("_1"))
					).select(joinRes.col("_2"));
			rightOnlyDS.show();
			System.out.println();
		} finally {
			diffDS.unpersist();
		}
	}
}