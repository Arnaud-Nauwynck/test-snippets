package fr.an.tests.dockerjib;

import java.util.Map;
import java.util.Properties;

public class App {

	public static void main(String[] args) {
		System.out.println("Hello world (v2)!");
		
		int argIdx = 0;
		for(String arg : args) {
			System.out.println("arg[" + (argIdx++) + "]: " + arg);
		}
		
		Properties sysProps = System.getProperties();
		System.out.println("system variables:" + sysProps);
		
		Map<String, String> envVars = System.getenv();
		System.out.println("environments variables:" + envVars);
	}
}
