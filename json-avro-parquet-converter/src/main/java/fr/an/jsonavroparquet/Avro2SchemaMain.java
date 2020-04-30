package fr.an.jsonavroparquet;

import java.io.File;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.FileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.io.FileUtils;

import lombok.Getter;
import lombok.Setter;

public class Avro2SchemaMain {

	@Getter @Setter
	private String inputPath;

	@Getter @Setter
	private String outputPath;
	
	public static void main(String[] args) {
		Avro2SchemaMain app = new Avro2SchemaMain();
		try {
			app.parseArgs(args);
			app.run();
		} catch(Exception ex) {
			System.err.println("Failed");
			ex.printStackTrace(System.err);
		}
	}
	
	public void parseArgs(String[] args) {
		this.inputPath = args[0];
		this.outputPath = args[1];
	}
	
	public void run() throws Exception {
		GenericDatumReader<GenericRecord> genericAvroReader = new GenericDatumReader<GenericRecord>();
        FileReader<GenericRecord> fileReader = DataFileReader.openReader(new File(inputPath), genericAvroReader);
        Schema avroSchema = fileReader.getSchema();

        String schemaString = avroSchema.toString(true);
        System.out.println("schema:" + schemaString);
        FileUtils.write(new File(outputPath), schemaString, "UTF-8");
	}

}
