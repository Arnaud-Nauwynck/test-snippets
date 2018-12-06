package fr.an.tests.springbootproploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

// referenced from META-INF/spring.factories
public class AppApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	
	private static final Logger log = LoggerFactory.getLogger(AppApplicationContextInitializer.class);

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		log.info("AppApplicationContextInitializer.initialize()");
	}

}
