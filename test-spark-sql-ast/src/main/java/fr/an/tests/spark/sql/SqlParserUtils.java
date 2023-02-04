package fr.an.tests.spark.sql;

import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;
import org.apache.spark.sql.execution.SparkSqlParser;

import lombok.val;

public class SqlParserUtils {

	public static LogicalPlan parsePlan(String sql) {
		SparkSqlParser parser = new SparkSqlParser();
		val res = parser.parsePlan(sql);
		return res;
	}
}
