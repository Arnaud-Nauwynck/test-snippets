package testspringbootprofile;

import java.net.URL;

public abstract class AbstractTest {

	static {
		System.out.println("### OVERRIDE org.springframework.boot.logging.LoggingSystem=none");
		System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
		
		ClassLoader cl = AbstractTest.class.getClassLoader();
		logResourceUrl(cl, "logback-test.xml");
		logResourceUrl(cl, "logback.xml");
	}

	private static void logResourceUrl(ClassLoader cl, String resourceName) {
		URL resourceUrl = cl.getResource(resourceName);
		System.out.println(resourceName + " => " + resourceUrl);
	}
}
