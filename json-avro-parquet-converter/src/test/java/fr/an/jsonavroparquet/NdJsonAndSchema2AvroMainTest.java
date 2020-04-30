package fr.an.jsonavroparquet;

import java.io.File;

import org.junit.Test;

import fr.an.jsonavroparquet.Avro2JsonMain;
import fr.an.jsonavroparquet.NdJsonAndSchema2AvroMain;

public class NdJsonAndSchema2AvroMainTest {

	@Test
	public void test() throws Exception {
		NdJsonAndSchema2AvroMain app = new NdJsonAndSchema2AvroMain();
		app.setInputPath("data/test1/userdata1.ndjson");
		app.setInputAvroSchemaPath("data/test1/user.avsc");
		File destDir = new File("target/data");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		app.setOutputPath("target/data/userdata1-fromJson.avro");
		
		app.run();
		
		{ // convert back to check..
			Avro2JsonMain checkApp = new Avro2JsonMain();
			checkApp.setInputPath(app.getOutputPath());
			checkApp.setOutputPath("target/data/userdata1-fromAvro-fromJson.ndjson");
			
			checkApp.run();
		}
	}
}
