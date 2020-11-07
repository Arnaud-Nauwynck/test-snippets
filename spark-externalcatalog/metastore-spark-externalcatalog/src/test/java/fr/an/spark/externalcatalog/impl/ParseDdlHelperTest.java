package fr.an.spark.externalcatalog.impl;

import java.net.URI;

import org.apache.spark.sql.catalyst.catalog.CatalogStorageFormat;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.plans.logical.CreateNamespaceStatement;
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;
import org.apache.spark.sql.execution.datasources.CreateTable;
import org.apache.spark.sql.types.StructType;
import org.junit.Assert;
import org.junit.Test;

import fr.an.metastore.spark.impl.ParseDdlHelper;
import scala.Option;
import scala.collection.Seq;

public class ParseDdlHelperTest {

	@Test
	public void testParseCreateDatabase() {
//	    | CREATE namespace (IF NOT EXISTS)? multipartIdentifier
//	            (commentSpec |
//	             locationSpec |
//	             (WITH (DBPROPERTIES | PROPERTIES) tablePropertyList))*        #createNamespace

	            
		String sql= "CREATE database testdb " 
				+ "COMMENT \"comment1\" "
				+ "LOCATION \"file://dir1\" "
				+" WITH PROPERTIES (" 
				+ " prop1 = \"value1\","
				+ " prop2 = \"value2\""
				+ ")";
		LogicalPlan res = ParseDdlHelper.parse(sql);
		Assert.assertNotNull(res);

		CreateNamespaceStatement c = (CreateNamespaceStatement) res;
		String dbName = c.namespace().apply(0);
		Assert.assertEquals("testdb", dbName);
		
		scala.collection.immutable.Map<String, String> props = c.properties();
		// String loc = props.get("location").get(); // eclipse compile bug!!?!
		scala.collection.Map<String,String> props2 = props;
		String loc = props2.get("location").get();
		Assert.assertEquals("file://dir1", loc);
		String comment = props2.get("comment").get();
		Assert.assertEquals("comment1", comment);
		String prop1 = props2.get("prop1").get();
		Assert.assertEquals("value1", prop1);

	}
	
	@Test
	public void testParseCreateTable() {
		String sql= "CREATE EXTERNAL TABLE IF NOT EXISTS db1.employee " + 
				"( eid int, name String, \n" + 
				"salary String, destination String)\n" + 
				"PARTITIONED BY (department String)\n" +
				"LOCATION 'file://dir1'\n" +
				"COMMENT 'Employee details'\n" + 
				"ROW FORMAT DELIMITED\n" + 
				"FIELDS TERMINATED BY '\\t'\n" + 
				"LINES TERMINATED BY '\\n'\n" + 
				"STORED AS TEXTFILE";
		LogicalPlan res = ParseDdlHelper.parse(sql);
		Assert.assertNotNull(res);
		CreateTable c = (CreateTable) res;
		
		CatalogTable tableDesc = c.tableDesc();
		
		String qn = tableDesc.qualifiedName();
		Assert.assertEquals("db1.employee", qn);
		
		StructType schema = tableDesc.schema();
		
		Seq<String> partitionColNames = tableDesc.partitionColumnNames();
		
		CatalogStorageFormat storage = tableDesc.storage();
		Option<URI> storageLoc = storage.locationUri();
		
		Option<String> serde = storage.serde();
		
	}
	
//	@Test
//	public void testParseList() {
//		String sql= "CREATE database testdb1;" 
//				+ "CREATE database testdb2;";
//		LogicalPlan res = ParseDdlHelper.parse(sql);  ... FAIL
//		Assert.assertNotNull(res);
//	}

}
