package org.example;

import org.apache.spark.sql.SparkSession;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.URL;

@SpringBootApplication
public class Springboot3SparkWithRepackagedMain {

    public static void main(String[] args) {
        ClassLoader cl = Springboot3SparkWithRepackagedMain.class.getClassLoader();
        checkClassLoaderResource(cl, "javax/servlet/Servlet.class");

        SpringApplication.run(Springboot3SparkWithRepackagedMain.class, args);
    }

    private static void checkClassLoaderResource(ClassLoader cl, String resource) {
        URL servletClassResource = cl.getResource(resource);
        System.out.println("classloader resource for: " + resource + " => " + servletClassResource);
    }

}

@Configuration
class SparkConfiguration {

    @Bean
    public SparkSession sparkSession() {
        System.out.println("SparkSession getOrCreate");
        return SparkSession.builder().appName("test-springboot-spark")
                .master("local[*]")
                // .enableHiveSupport()
                .getOrCreate();
    }
}

@Component
class CmdRunner implements CommandLineRunner {

    protected final SparkSession sparkSession;

    public CmdRunner(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("spark.sql ...");
        sparkSession.sql("SELECT 1").show();
    }

}