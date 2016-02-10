package fr.an.tests.testjson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
public class AppMain {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AppMain.class, args);
    }
    
    
}