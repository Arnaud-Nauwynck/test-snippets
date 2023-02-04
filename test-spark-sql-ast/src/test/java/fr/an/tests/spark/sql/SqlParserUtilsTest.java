package fr.an.tests.spark.sql;

import org.junit.Assert;
import org.junit.Test;

import lombok.val;

public class SqlParserUtilsTest {

	@Test
	public void testParsePlan() {
		val ast = SqlParserUtils.parsePlan("INSERT INTO db1.t1 SELECT * from db1.t2");
		Assert.assertNotNull(ast);
//		'InsertIntoStatement 'UnresolvedRelation [db1, t1], [], false, false, false
//		+- 'Project [*]
//		   +- 'UnresolvedRelation [db1, t2], [], false
		   
	}
}
