package fr.an.tests.springbootproploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

// referenced from META-INF/spring.factories
public class AppApplicationListener implements ApplicationListener<ApplicationEvent> {
	
	private static final Logger log = LoggerFactory.getLogger(AppApplicationListener.class);

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		log.info("AppApplicationListener.onApplicationEvent(" + event + ")");
	}
	
}
