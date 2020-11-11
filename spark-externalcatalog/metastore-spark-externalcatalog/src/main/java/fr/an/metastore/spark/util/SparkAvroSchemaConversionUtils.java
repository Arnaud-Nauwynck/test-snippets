package fr.an.metastore.spark.util;

import org.apache.avro.Schema;
import org.apache.spark.sql.avro.SchemaConverters.SchemaType;
import org.apache.spark.sql.types.StructType;

import fr.an.metastore.api.immutable.ImmutableStructType;
import fr.an.metastore.api.utils.NotImpl;

public class SparkAvroSchemaConversionUtils {

	public static StructType avroSchemaToSparkStructType(Schema avroSchema) {
		SchemaType sqlType = org.apache.spark.sql.avro.SchemaConverters.toSqlType(avroSchema);
		return (StructType) sqlType.dataType();
	}

	public static StructType schemaDefToSparkStructType(ImmutableStructType schemaDef) {
		StructType res = (StructType) schemaDef.getAsSparkStruct();
		if (res == null) {
			Schema avroSchema = schemaDef.getAsAvroSchema();
			if (avroSchema != null) {
				// Convert Avro Schema -> Spark StructType
				StructType structType = SparkAvroSchemaConversionUtils.avroSchemaToSparkStructType(avroSchema);
				schemaDef.setAsSparkStruct(structType);
				res = structType;
			}
			
			if (res == null) {
				throw NotImpl.notImplEx();
			}
		}
		return res;
	}
	
}
