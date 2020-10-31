package fr.an.metastore.spark.impl;

import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;
import org.apache.spark.sql.execution.SparkSqlParser;
import org.apache.spark.sql.internal.SQLConf;

public class ParseDdlHelper {

	public static LogicalPlan parse(String command) {
		SQLConf sparSQLConf = new SQLConf();
		SparkSqlParser parser = new SparkSqlParser(sparSQLConf);
		
		LogicalPlan plan = parser.parsePlan(command);
		
		return plan;
	}
}
