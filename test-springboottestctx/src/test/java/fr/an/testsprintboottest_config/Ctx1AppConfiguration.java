package fr.an.testsprintboottest_config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan("fr.an.testsprintboottest")
public class Ctx1AppConfiguration {

	private static final Logger log = LoggerFactory.getLogger(Ctx1AppConfiguration.class);
	
	public static class BeanCtx1 {
	}

	@Bean 
	public BeanCtx1 beanCtx1() {
		log.info("Ctx1AppConfiguration.beanCtx1");
		return new BeanCtx1();
	}
	
}
