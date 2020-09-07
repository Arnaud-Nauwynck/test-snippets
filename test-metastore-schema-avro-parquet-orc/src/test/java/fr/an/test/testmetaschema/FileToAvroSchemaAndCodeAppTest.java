package fr.an.test.testmetaschema;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import lombok.val;

public class FileToAvroSchemaAndCodeAppTest {

	@Test
	public void testORCFile() throws Exception {
		// Given
		String inputPath = "src/test/data/TestOrcFile.test1.orc"; // file from https://github.com/apache/orc/blob/master/examples/TestOrcFile.test1.orc
		val app = new FileToAvroSchemaAndCodeApp();
		app.setInputPath(inputPath);
		app.setFileFormat("orc");
		File outputDir = new File("target/test-output/test-orc1");
		outputDir.mkdirs();
		app.setOutputDir(outputDir);

		// When
		app.run();
		
		// Then
		Assert.assertTrue(new File(outputDir, "record_0.java").exists());
		Assert.assertTrue(new File(outputDir, "record_10.java").exists());
		Assert.assertTrue(new File(outputDir, "record_12.java").exists());
		Assert.assertTrue(new File(outputDir, "record_16.java").exists());
		Assert.assertTrue(new File(outputDir, "record_21.java").exists());
	}

	@Test
	public void testAvroFile() throws Exception {
		// Given
		String inputPath = "./src/test/data/weather.avro"; // file from https://github.com/apache/avro/blob/master/share/test/data/weather.avro
		val app = new FileToAvroSchemaAndCodeApp();
		app.setInputPath(inputPath);
		app.setFileFormat("avro");
		File outputDir = new File("target/test-output/test-avro");
		outputDir.mkdirs();
		app.setOutputDir(outputDir);

		// When
		app.run();
		
		// Then
		Assert.assertTrue(new File(outputDir, "test/Weather.java").exists());
	}
	
	@Test
	public void testParquetFile() throws Exception {
		// Given
		String inputPath = "./src/test/data/users.parquet"; // from  from https://github.com/apache/spark/blob/master/examples/src/main/resources/users.parquet
		val app = new FileToAvroSchemaAndCodeApp();
		app.setInputPath(inputPath);
		app.setFileFormat("parquet");
		File outputDir = new File("target/test-output/test-parquet");
		outputDir.mkdirs();
		app.setOutputDir(outputDir);

		// When
		app.run();
		
		// Then
		Assert.assertTrue(new File(outputDir, "example/avro/User.java").exists());
	}
	
}
