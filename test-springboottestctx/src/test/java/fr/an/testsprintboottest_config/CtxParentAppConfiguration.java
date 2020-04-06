package fr.an.testsprintboottest_config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import fr.an.testsprintboottest.service.Bar;

@EnableAutoConfiguration
@ComponentScan("fr.an.testsprintboottest")
public class CtxParentAppConfiguration {

	
	private static final Logger log = LoggerFactory.getLogger(CtxParentAppConfiguration.class);

	@PostConstruct
	public void init() {
		log.info("CtxParentAppConfiguration.init()");
	}
	
	@Bean
	public Bar bar() {
		log.info("CtxParentAppConfiguration.bar()");
		return new Bar();
	}
}
