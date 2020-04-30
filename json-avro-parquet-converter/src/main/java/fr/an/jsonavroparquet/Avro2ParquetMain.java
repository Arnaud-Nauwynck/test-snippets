package fr.an.jsonavroparquet;

import java.io.File;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.FileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;

import lombok.Getter;
import lombok.Setter;

public class Avro2ParquetMain {

	@Getter @Setter
	private String inputPath;

	@Getter @Setter
	private String outputPath;
	
	@Getter @Setter
	private CompressionCodecName compressionCodecName = CompressionCodecName.UNCOMPRESSED;

	@Getter @Setter
	private int blockSize = 256 * 1024 * 1024;

	@Getter @Setter
	private int pageSize = 64 * 1024;
	
	public static void main(String[] args) {
		Avro2ParquetMain app = new Avro2ParquetMain();
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

        File f = new File(outputPath);
        if(f.exists()){
            f.delete();
        }
        Path hadoopOutputPath = new Path(outputPath);

        ParquetWriter<GenericRecord> parquetWriter = AvroParquetWriter.<GenericRecord>builder(hadoopOutputPath)
        		.withSchema(avroSchema)
        		.withCompressionCodec(compressionCodecName)
        		.withRowGroupSize(blockSize)
        		.withPageSize(pageSize)
        		.build();

       
        DataFileReader<GenericRecord> reader = new DataFileReader<GenericRecord>(new File(inputPath), 
        		new GenericDatumReader<GenericRecord>());
        while (reader.hasNext()) {
            GenericRecord record = reader.next();
            parquetWriter.write(record);
        }
       
        parquetWriter.close();
	}

}
