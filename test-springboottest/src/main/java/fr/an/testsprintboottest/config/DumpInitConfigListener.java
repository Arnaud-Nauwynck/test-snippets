package fr.an.testsprintboottest.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DumpInitConfigListener {

	private static final Logger log = LoggerFactory.getLogger(DumpInitConfigListener.class);

	int appStartedCount = 0;
	
	@EventListener
	public void onAppInit(ApplicationStartedEvent event) {
		ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
		log.info("########## app.started " + (++appStartedCount)  
				+ " activeProfiles:" + Arrays.asList(environment.getActiveProfiles())
				+ " app.key:" + environment.getProperty("app.key")
				+ " environment: " + environment
				);
	}

}
