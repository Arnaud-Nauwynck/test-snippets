package fr.an.tests.eclipselink.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.config.BeanPostProcessor;

// @Component
public class DataSourceInstrumenterPostProcessorConfig implements BeanPostProcessor {


	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		if (bean instanceof DataSource) {
//			if (bean instanceof HikariDataSource) {
//				
//			}
//			if (bean instanceof org.apache.tomcat.jdbc.pool.DataSource) {
//				TomcatPoolDataSourceProxyInstrumenter.injectSefDataSourceProxyInto((org.apache.tomcat.jdbc.pool.DataSource) bean);
//			}
		}
		return bean;
	}

}
