package fr.an.test.testmetaschema.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.avro.Schema;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroSchemaConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.schema.MessageType;

import lombok.val;

public class ParquetToAvroSchemaUtils {
	
	private ParquetToAvroSchemaUtils() {}
	
	public static Schema parquetFilePathToAvroSchema(Configuration hadoopCfg, Path parquetPath) throws IOException {
	    HadoopInputFile parquetInputFile = HadoopInputFile.fromPath(parquetPath, hadoopCfg);
	    return parquetFileToAvroSchema(hadoopCfg, parquetInputFile);
	}

	public static Schema parquetFileToAvroSchema(Configuration hadoopCfg, HadoopInputFile parquetInputFile) throws IOException {
		val parquetSchema = parquetFileToSchema(parquetInputFile);
		return parquetSchemaToAvroSchema(hadoopCfg, parquetSchema);
	}
	
	public static MessageType parquetFileToSchema(HadoopInputFile parquetInputFile) throws IOException {
		try(val rdr = ParquetFileReader.open(parquetInputFile)) {
	      val schema = rdr.getFooter().getFileMetaData().getSchema();
	      return schema;
	    }
	}
	
	public static Schema parquetSchemaToAvroSchema(Configuration hadoopCfg, MessageType parquetSchema) throws IOException {
		AvroSchemaConverter avroSchemaConverter = new AvroSchemaConverter(hadoopCfg);
		val avroSchema = avroSchemaConverter.convert(parquetSchema);
		return avroSchema;
	}

	public static void writeToLocalFile(Schema schema, File outputFile) {
		String schemaJson = schema.toString(true);
		byte[] content = schemaJson.getBytes();
		try(OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
			out.write(content);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to write schema to file " + outputFile, ex);
		}
	}
}
