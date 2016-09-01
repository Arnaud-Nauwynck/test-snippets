package fr.an.tests.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.jaxrs.config.BeanConfig;

@Configuration
public class SwaggerConfiguration {

	@Bean
	public BeanConfig swaggerConfig() {
		BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        // beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setBasePath("/rest");
        beanConfig.setResourcePackage("fr.an.tests.rest");
        beanConfig.setPrettyPrint(true);
        beanConfig.setScan(true);
        return beanConfig;
	}
	
}
