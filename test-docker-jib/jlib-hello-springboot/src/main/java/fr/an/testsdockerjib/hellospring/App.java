package fr.an.testsdockerjib.hellospring;

import java.util.Map;
import java.util.Properties;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		System.out.println("Hello springboot (v1)!");
		
		int argIdx = 0;
		for(String arg : args) {
			System.out.println("arg[" + (argIdx++) + "]: " + arg);
		}
		
		Properties sysProps = System.getProperties();
		System.out.println("system variables:" + sysProps);
		
		Map<String, String> envVars = System.getenv();
		System.out.println("environments variables:" + envVars);

		SpringApplication.run(App.class, args);
	}
}

@Component
class AppCommandRunner implements CommandLineRunner {
	
	@Override
	public void run(String... args) throws Exception {
		System.out.println("after parse springboot => args:" + args);
	}
	
}
