package fr.an.testsprintboottest;

import org.springframework.boot.SpringApplication;

import fr.an.testsprintboottest.config.AppConfiguration;

public class AppMain {

	public static void main(String[] args) {
		SpringApplication.run(AppConfiguration.class, args);
	}
	
}
