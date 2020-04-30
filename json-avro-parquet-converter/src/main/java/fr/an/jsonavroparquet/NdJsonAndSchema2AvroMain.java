package fr.an.jsonavroparquet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.an.jsonavroparquet.impl.JsonObjToAvroRecordConverter;
import lombok.Getter;
import lombok.Setter;

public class NdJsonAndSchema2AvroMain {

	@Getter
	@Setter
	private String inputPath;

	@Getter
	@Setter
	private String inputAvroSchemaPath;

	@Getter
	@Setter
	private String outputPath;

	@Getter
	@Setter
	private ObjectMapper jsonMapper;

	public static void main(String[] args) {
		NdJsonAndSchema2AvroMain app = new NdJsonAndSchema2AvroMain();
		try {
			app.parseArgs(args);
			app.run();
		} catch (Exception ex) {
			System.err.println("Failed");
			ex.printStackTrace(System.err);
		}
	}

	public void parseArgs(String[] args) {
		this.inputPath = args[0];
		this.inputAvroSchemaPath = args[1];
		this.outputPath = args[2];
	}

	public void run() throws Exception {
		if (jsonMapper == null) {
			jsonMapper = new ObjectMapper();
		}
		
		Schema avroSchema;
		try (InputStream schemaInput = new BufferedInputStream(new FileInputStream(inputAvroSchemaPath))) {
			avroSchema = new Schema.Parser().parse(schemaInput);
		}
		
		JsonObjToAvroRecordConverter jsonToAvroConverter = new JsonObjToAvroRecordConverter();
		try (BufferedReader lineReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)))) {
			
			try (DataFileWriter<GenericData.Record> avroDataFileWriter = new DataFileWriter<>(new GenericDatumWriter<>(avroSchema))) {
//		        if (codecFactory != null) {
//		            writer.setCodec(codecFactory);
//		        }
				avroDataFileWriter.create(avroSchema, new File(outputPath));
				
				String line;
				while (null != (line = lineReader.readLine())) {
					@SuppressWarnings("unchecked")
					Map<String,Object> jsonObj = jsonMapper.readValue(line, Map.class);
					
					System.out.println("record: " + jsonObj);
					GenericData.Record avroRecord = jsonToAvroConverter.convertJsonToAvroRecord(jsonObj, avroSchema); 

					System.out.println(".. converted avro record: " + avroRecord.toString());
					
					avroDataFileWriter.append(avroRecord);
		            
				}
			}
		}
	}

	
    
}
