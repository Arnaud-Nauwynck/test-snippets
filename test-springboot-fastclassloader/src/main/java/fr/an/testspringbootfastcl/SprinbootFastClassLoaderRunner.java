package fr.an.testspringbootfastcl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(lazyInit = true)
public class SprinbootFastClassLoaderRunner {
	
	public static void run(String[] args) {
		long startTime = System.currentTimeMillis();

		
		ConfigurableApplicationContext appCtx = SpringApplication.run(SprinbootFastClassLoaderRunner.class, args);
		
		long startMillis = System.currentTimeMillis() - startTime;
		System.out.println("startup time: " + startMillis + " ms");
		
		
		long closeTime = System.currentTimeMillis();
		appCtx.close();
		System.out.println("ctx close .. exiting");
		long closeMillis = System.currentTimeMillis() - closeTime;
		System.out.println("close time: " + closeMillis + " ms");
	}

}
