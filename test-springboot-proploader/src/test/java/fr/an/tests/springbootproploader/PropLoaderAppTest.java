package fr.an.tests.springbootproploader;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
		properties = "app.propTest1=@SpringbootTest-propTest1"
		// , classes = PropLoaderApp.class .. default, load @SpringBootApplication class  
		)
@ActiveProfiles(
		profiles = "testprofile1"
		// resolver = ""
		)
public class PropLoaderAppTest {

}
