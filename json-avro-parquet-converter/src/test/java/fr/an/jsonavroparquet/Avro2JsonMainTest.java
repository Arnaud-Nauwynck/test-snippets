package fr.an.jsonavroparquet;

import java.io.File;

import org.junit.Test;

import fr.an.jsonavroparquet.Avro2JsonMain;

public class Avro2JsonMainTest {

	@Test
	public void test() throws Exception {
		Avro2JsonMain app = new Avro2JsonMain();
		app.setInputPath("data/test1/userdata1.avro");
		File destDir = new File("target/data");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		app.setOutputPath("target/data/userdata1.ndjson");
		
		app.run();
	}
}
