package fr.an.jsonavroparquet;

import java.io.File;

import org.junit.Test;

import fr.an.jsonavroparquet.Avro2ParquetMain;

public class Avro2ParquetMainTest {

	@Test
	public void test() throws Exception {
		Avro2ParquetMain app = new Avro2ParquetMain();
		app.setInputPath("data/test1/userdata1.avro");
		File destDir = new File("target/data");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		app.setOutputPath("target/data/userdata1.parquet");
		
		app.run();
	}
}
