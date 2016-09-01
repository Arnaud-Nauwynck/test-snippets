package fr.an.tests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration(exclude={
		// org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration.class // when activated => no more static resources!!!!
		})
public class JerseySwaggerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JerseySwaggerApplication.class, args);
	}
}
