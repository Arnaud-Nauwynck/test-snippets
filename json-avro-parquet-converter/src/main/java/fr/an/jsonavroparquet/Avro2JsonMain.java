package fr.an.jsonavroparquet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;

import lombok.Getter;
import lombok.Setter;

public class Avro2JsonMain {

	@Getter @Setter
	private String inputPath;

	@Getter @Setter
	private String outputPath;
	
	public static void main(String[] args) {
		Avro2JsonMain app = new Avro2JsonMain();
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
       
		try (Writer out = new OutputStreamWriter(
				new BufferedOutputStream(new FileOutputStream(new File(outputPath))),
				"UTF-8")) {
			
			GenericDatumReader<GenericRecord> genericAvroReader = new GenericDatumReader<GenericRecord>();
	        try (DataFileReader<GenericRecord> reader = new DataFileReader<GenericRecord>(new File(inputPath), genericAvroReader)) {

		        while (reader.hasNext()) {
		            GenericRecord record = reader.next(); // GenericData.Record
		            String recordString = record.toString();
		            // System.out.println("record:" + recordString);
		            out.write(recordString);
		            out.write("\n");
		        }
	        }
		}
        
	}

}
