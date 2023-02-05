package fr.an.tests.spark.sql;

import org.junit.Assert;
import org.junit.Test;

import lombok.val;

public class SparkSqlAstSummaryPrinterTest {

	@Test
	public void testToSummary_select() {
		val sql = "SELECT a from db1.t1 WHERE b=2";
		val res = doParseAndToSummary(sql);
		Assert.assertEquals("SELECT /*1*/ FROM db1.t1 WHERE /*..*/", res);
	}

	@Test
	public void testToSummary_select_innerjoin() {
		val sql = "SELECT t.a from db1.t1 t" 
				+ " INNER JOIN db2.joinedT2 t2 ON t1.id=t2.id";
		val res = doParseAndToSummary(sql);
		Assert.assertEquals("SELECT /*1*/ FROM db1.t1 t JOIN db2.joinedT2 t2 /*..*/", res);
	}

	@Test
	public void testToSummary_select_join_join() {
		val sql = "SELECT t.a from db1.t1 t" //
				+ " JOIN db2.joinedT2 t2 ON t.id=t2.id" // 
				+ " JOIN db2.joinedT3 t3 ON t.id=t3.id";
		val res = doParseAndToSummary(sql);
		Assert.assertEquals("SELECT /*1*/ FROM db1.t1 t JOIN db2.joinedT2 t2 /*..*/ JOIN db2.joinedT3 t3 /*..*/", res);
	}

	@Test
	public void testToSummary_with_select() {
		val sql = "WITH t0 as (SELECT b from db1.t2)" 
					+" SELECT t.a from db1.t1 t";
		val res = doParseAndToSummary(sql);
		Assert.assertEquals("WITH t0 AS (SELECT /*1*/ FROM db1.t2 t0)" 
				+ " SELECT /*1*/ FROM db1.t1 t", res);
	}
	
	@Test
	public void testToSummary_insert_select() {
		val sql = "INSERT INTO db1.t2 SELECT a from db1.t1 WHERE b=2";
		val res = doParseAndToSummary(sql);
		Assert.assertEquals("INSERT INTO db1.t2 SELECT /*1*/ FROM db1.t1 WHERE /*..*/", res);
	}

	@Test
	public void testToSummary_insertOverwrite_select() {
		val sql = "INSERT OVERWRITE db1.t2 SELECT a from db1.t1 WHERE b=2";
		val res = doParseAndToSummary(sql);
		Assert.assertEquals("INSERT OVERWRITE db1.t2 SELECT /*1*/ FROM db1.t1 WHERE /*..*/", res);
	}
	
	private static String doParseAndToSummary(String sql) {
		val ast = SqlParserUtils.parsePlan(sql);
		val text = SparkSqlAstSummaryPrinter.toSummary(ast);
		Assert.assertNotNull(text);
		return text;
	}
}
