package fr.an.test.testmetaschema;

import java.io.File;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.orc.OrcFile;
import org.apache.orc.TypeDescription;
import org.apache.parquet.hadoop.util.HadoopInputFile;

import fr.an.test.testmetaschema.utils.AvroSpecificCompilerHelper;
import fr.an.test.testmetaschema.utils.AvroSpecificCompilerHelper.CompilerParams;
import fr.an.test.testmetaschema.utils.ParquetToAvroSchemaUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class FileToAvroSchemaAndCodeApp {

	@Getter @Setter
	private String inputPath;
	
	@Getter @Setter
	private String fileFormat = "parquet";
	
	@Getter @Setter
	private File outputDir = new File("output");
	
	@Getter @Setter
	private CompilerParams compilerParams = new CompilerParams();
	
	public static void main(String[] args) {
		try {
			FileToAvroSchemaAndCodeApp app = new FileToAvroSchemaAndCodeApp();
			app.parseArgs(args);
			app.run();
			System.out.println("Finished");
		} catch(Exception ex) {
			System.err.println("Failed, exiting");
		}
	}

	private void parseArgs(String[] args) {
		for(int i = 0; i < args.length; i++) {
			String a = args[i];
			if (a.equals("-i")) {
				inputPath = args[++i];
			} else {
				throw new IllegalArgumentException("unrecognized argument '" + a + "'");
			}
		}
	}

	public void run() throws IOException {
		Configuration hadoopCfg = new Configuration();
		Path hadoopInputPath = new Path(inputPath);

		Schema avroSchema;
		if (fileFormat.equals("avro")) {
			GenericDatumReader<GenericRecord> reader = new GenericDatumReader<>();
			DataFileReader<GenericRecord> dataFileReader;
			if (inputPath.startsWith("./")) {
				dataFileReader = new DataFileReader<>(new File(inputPath), reader);
			} else if (inputPath.startsWith("file://")) {
				dataFileReader = new DataFileReader<>(new File(inputPath), reader);
			} else {
				throw new RuntimeException("NOT IMPLEMENTED YET; reading avro from file scheme " + inputPath + " .. supports file://");
			}
			avroSchema = dataFileReader.getSchema();
			
		} else if (fileFormat.equals("parquet")) {
		    HadoopInputFile parquetInputFile = HadoopInputFile.fromPath(hadoopInputPath, hadoopCfg);
	
			val parquetSchema = ParquetToAvroSchemaUtils.parquetFileToSchema(parquetInputFile);
			// System.out.println("Parquet schema:" + parquetSchema);
			
			avroSchema = ParquetToAvroSchemaUtils.parquetSchemaToAvroSchema(hadoopCfg, parquetSchema);
			// System.out.println("=> convert Avro schema:" + avroSchema);
		} else if (fileFormat.equals("orc")) {
			org.apache.orc.Reader orcReader = OrcFile.createReader(hadoopInputPath,
	                  OrcFile.readerOptions(hadoopCfg));
			TypeDescription orcSchema = orcReader.getSchema();
			// System.out.println("ORC schema:" + orcSchema);
			
			avroSchema = org.apache.orc.bench.convert.avro.AvroSchemaUtils.createAvroSchema(orcSchema);
			
		} else {
			throw new RuntimeException("unknown file type, expecting avro | parquet | orc");
		}
		
		AvroSpecificCompilerHelper compilerHelper = new AvroSpecificCompilerHelper(compilerParams);
		compilerHelper.compileToDestination(avroSchema, outputDir);
		
	}

}
