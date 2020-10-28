package fr.an.tests.testspark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.BeforeClass;
import org.junit.Test;

public class SparkCreateTempViewTest extends AbstractSparkTestCase {

	@BeforeClass
	public static void onStart() {
		startSparkSession();
	}

	@Test
	public void testCreateGlobalTempView() throws Exception {
		Dataset<Row> srcDs = spark.read().format("csv").option("header","true")
				.load("src/test/data/file1.csv");
		
		srcDs.createGlobalTempView("globalTempView1");

		{ System.out.println("test global_temp.globalTempView1");
		Dataset<Row> ds2 = spark.sqlContext().sql("select * from global_temp.globalTempView1");
		ds2.limit(2).show();
		}

		{ // case incensitive
			System.out.println("test global_temp.globaltempview1");
			Dataset<Row> ds = spark.sqlContext().sql("select * from global_temp.globaltempview1");
			ds.limit(2).show();
		}
		

		try { 
			System.out.println("test globalTempView1");
			Dataset<Row> ds3 = spark.sqlContext().sql("select * from globalTempView1");
			ds3.limit(2).show();
		} catch(Exception ex) {
			System.out.println("missing glob_temp. ?  " + ex.getMessage());
		}
		
		// test compute ds and persist, then drop temp table
		Dataset<Row> lazyDs = spark.sqlContext().sql("select * from global_temp.globalTempView1");
		Dataset<Row> persistDs = spark.sqlContext().sql("select * from global_temp.globalTempView1");
		persistDs.persist();
		
		spark.catalog().dropGlobalTempView("globalTempView1");

		try {
			System.out.println("lazyDs show after dropGlobalTempView(globalTempView1)");
			lazyDs.show();
		} catch(Exception ex) {
			System.out.println("lazyDs .. while globalTempView1 dropped");
		}
		System.out.println("ds persist.. ");
		persistDs.limit(2).show();
		persistDs.unpersist();
		
	}

	@Test
	public void testCreateTempView() throws Exception {
		Dataset<Row> srcDs = spark.read().format("csv").option("header","true")
				.load("src/test/data/file1.csv");
		
		srcDs.createTempView("tempView1");

		{ 
			System.out.println("test tempView1");
			Dataset<Row> ds3 = spark.sqlContext().sql("select * from tempView1");
			ds3.limit(2).show();
		}

		{  // case incensitive 
			System.out.println("test tempview1");
			Dataset<Row> ds = spark.sqlContext().sql("select * from tempview1");
			ds.limit(2).show();
		}

		try { 
			System.out.println("test global_temp.tempView1");
			Dataset<Row> ds2 = spark.sqlContext().sql("select * from global_temp.tempView1");
			ds2.limit(2).show();
		} catch(Exception ex) {
			System.out.println("extra glob_temp. ?" + ex.getMessage());
		}		

		// test compute sub-ds, persist, then drop temp table
		Dataset<Row> lazyDs = spark.sqlContext().sql("select * from tempView1");
		Dataset<Row> persistDs = spark.sqlContext().sql("select * from tempView1");
		persistDs.persist();

		spark.catalog().dropTempView("tempView1");
		
		try {
			System.out.println("lazyDs show after dropTempView");
			lazyDs.show();
			System.out.println("?????????? lazyDs show after dropTempView !");
		} catch(Exception ex) {
			System.out.println("lazyDs .. while tempView1 dropped");
		}
		
		persistDs.limit(2).show();
		persistDs.unpersist();

	}

}
