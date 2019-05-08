package fr.an.testsprintboottest_config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan("fr.an.testsprintboottest")
public class Ctx2AppConfiguration {
	
	private static final Logger log = LoggerFactory.getLogger(Ctx2AppConfiguration.class);
	
	public static class BeanCtx2 {
	}

	@Bean 
	public BeanCtx2 beanCtx2() {
		log.info("Ctx2AppConfiguration.beanCtx2");
		return new BeanCtx2();
	}
	
}
