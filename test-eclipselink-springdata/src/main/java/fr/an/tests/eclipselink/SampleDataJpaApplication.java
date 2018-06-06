/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.an.tests.eclipselink;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class SampleDataJpaApplication extends JpaBaseConfiguration {

	public static void main(String[] args) throws Exception {
		// must run with jvm param 
		// -javaagent:$HOME/.m2/repository/org/springframework/spring-instrument/4.2.4.RELEASE/spring-instrument-4.2.4.RELEASE.jar
		SpringApplication.run(SampleDataJpaApplication.class, args);
	}
	
	@Override
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		AbstractJpaVendorAdapter adapter = createJpaVendorAdapter();
//		adapter.setShowSql(this.jpaProperties.isShowSql());
//		adapter.setDatabase(this.jpaProperties.getDatabase());
//		adapter.setDatabasePlatform(this.jpaProperties.getDatabasePlatform());
		
//		adapter.setDatabasePlatform("org.eclipse.persistence.platform.database.H2Platform");
		
//		adapter.setGenerateDdl(this.jpaProperties.isGenerateDdl());
		return adapter;
	}
	
	
	@Override
	protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
		EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();
		// properties overriden after!
//		adapter.setDatabase(Database.H2); // => ignored!
//		adapter.setDatabasePlatform("org.eclipse.persistence.platform.database.H2Platform");
		return adapter;
	}

	@Override
	protected Map<String, Object> getVendorProperties() {
		Map<String, Object> map = new HashMap<String, Object>();
		// map.put(PersistenceUnitProperties.TARGET_DATABASE, "org.eclipse.persistence.platform.database.H2Platform"); // => ignored determineDatabase("H2")!
		map.put("eclipselink.ddl-generation", "create-tables");
		map.put("eclipselink.ddl-generation.output-mode", "both");
		// map.put("eclipselink.ddl-generation.output-mode", "database");
		map.put("eclipselink.logging.level",
//				"INFO"
				"FINE"
//				"FINEST"
				);
		
		return map;
	}

}
