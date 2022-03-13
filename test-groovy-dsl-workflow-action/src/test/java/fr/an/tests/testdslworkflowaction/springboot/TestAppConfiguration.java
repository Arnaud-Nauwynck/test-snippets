package fr.an.tests.testdslworkflowaction.springboot;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableAutoConfiguration
@EnableConfigurationProperties(AppParams.class)
// @ComponentScan("fr.an.tests.testdslworkflowaction.springboot")
public class TestAppConfiguration {

	@Bean
	public AppParam2s appParam2s() {
		return new AppParam2s();
	}

}
