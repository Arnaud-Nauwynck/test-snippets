package fr.an.tests.springbootproploader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

// cf referenced from META-INF/spring.factories
public class AppPropertySourceLoader implements PropertySourceLoader {

	
	private static final Logger log = LoggerFactory.getLogger(AppPropertySourceLoader.class);

	@Override
	public String[] getFileExtensions() {
		log.info("AppPropertySourceLoader.getFileExtensions() => .."); 
		return new String[] { "encrypted-props" };
	}

	@Override
	public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
		log.info("AppPropertySourceLoader.load(" + name + "," + resource + ") => .."); 
		List<PropertySource<?>> res = new ArrayList<PropertySource<?>>();
		// TODO
		return res;
	}

	
}
