package fr.an.tools.git2neo4j.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Configuration
@ComponentScan("fr.an.tools.git2neo4j")
public class GitRepo2Neo4jConfig {

	@Bean
	public MapperFacade mapperFacade() {
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder()
				// .converterFactory(converterFactory)
				.build();
		return mapperFactory.getMapperFacade();
	}
	
}
