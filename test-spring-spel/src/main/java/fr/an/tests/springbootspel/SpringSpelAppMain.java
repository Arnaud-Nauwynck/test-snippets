package fr.an.tests.springbootspel;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringSpelAppMain {

	public static void main(String[] args) {
		SpringApplication.run(SpringSpelAppMain.class, args);
	}
	
}


class AppCmdLineRunner implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		// TODO
	}
	
}