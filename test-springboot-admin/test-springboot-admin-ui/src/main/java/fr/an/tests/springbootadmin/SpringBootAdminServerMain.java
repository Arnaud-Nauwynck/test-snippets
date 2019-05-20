package fr.an.tests.springbootadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

@SpringBootApplication
@EnableAdminServer
public class SpringBootAdminServerMain {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootAdminServerMain.class, args);
	}
	
}
