package fr.an.tests.springbootproploader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor;

// referenced from META-INF/spring.factories
public class AppSpringApplicationRunListener implements SpringApplicationRunListener {
	
	private static final Logger log = LoggerFactory.getLogger(AppSpringApplicationRunListener.class);

	public AppSpringApplicationRunListener(SpringApplication springApp, String[] args) {
		log.info("AppSpringApplicationRunListener(app, args: " + Arrays.asList(args) + ")");
	}
	
	@Override
	public void starting() {
		log.info("AppSpringApplicationRunListener.starting()");
	}

	@Override
	public void environmentPrepared(ConfigurableEnvironment environment) {
		log.info("AppSpringApplicationRunListener.environmentPrepared()");
		MutablePropertySources propertySources = environment.getPropertySources();
		propertySources.addLast(runListenerMapPropSource());
	}

	private static MapPropertySource runListenerMapPropSource() {
		Map<String,Object> map = new HashMap<>();
		map.put("app.runListenerMapPropSource-prop1", "runListenerMapPropSource().map.get(app.runListenerMapPropSource-prop1)");
		return new MapPropertySource("runListenerMapPropSource", map);
	}

	
	@Override
	public void contextPrepared(ConfigurableApplicationContext context) {
		log.info("AppSpringApplicationRunListener.contextPrepared()");
	}

	@Override
	public void contextLoaded(ConfigurableApplicationContext context) {
		log.info("AppSpringApplicationRunListener.contextLoaded()");
	}

	@Override
	public void started(ConfigurableApplicationContext context) {
		log.info("AppSpringApplicationRunListener.started()");
	}

	@Override
	public void running(ConfigurableApplicationContext context) {
		log.info("AppSpringApplicationRunListener.running()");
	}

	@Override
	public void failed(ConfigurableApplicationContext context, Throwable exception) {
		log.info("AppSpringApplicationRunListener.failed()");
	}
	
}
