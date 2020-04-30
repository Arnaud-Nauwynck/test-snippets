package fr.an.jsonavroparquet;

import java.io.File;

import org.junit.Test;

import fr.an.jsonavroparquet.Avro2SchemaMain;

public class Avro2SchemaMainTest {

	@Test
	public void test() throws Exception {
		Avro2SchemaMain app = new Avro2SchemaMain();
		app.setInputPath("data/test1/userdata1.avro");
		File destDir = new File("target/data");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		app.setOutputPath("target/data/userdata1.avsc");
		
		app.run();
	}
}
