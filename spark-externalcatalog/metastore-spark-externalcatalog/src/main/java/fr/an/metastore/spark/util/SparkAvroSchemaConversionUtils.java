package fr.an.metastore.spark.util;

import org.apache.avro.Schema;
import org.apache.spark.sql.avro.SchemaConverters.SchemaType;
import org.apache.spark.sql.types.StructType;

public class SparkAvroSchemaConversionUtils {

	public static StructType avroSchemaToSparkStructType(Schema avroSchema) {
		SchemaType sqlType = org.apache.spark.sql.avro.SchemaConverters.toSqlType(avroSchema);
		return (StructType) sqlType.dataType();
	}

}
