package fr.an.tests.testsparkxml.utils;

import lombok.val;

public class SparkAvroSchemaUtils {

	public static org.apache.avro.Schema sparkTypeToAvroSchema(
			org.apache.spark.sql.types.DataType sparkType) {
		val res = org.apache.spark.sql.avro.SchemaConverters.toAvroType(
				sparkType, true, "user", "usernamespace");
		return res;
	}

	public static org.apache.spark.sql.types.DataType avroSchemaToSparkType(
			org.apache.avro.Schema avroSchema) {
		val tmp = org.apache.spark.sql.avro.SchemaConverters.toSqlType(avroSchema);
		return tmp.dataType();
	}
}
