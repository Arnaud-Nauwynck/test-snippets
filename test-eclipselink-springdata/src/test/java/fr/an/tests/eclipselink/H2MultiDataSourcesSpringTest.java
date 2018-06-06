package fr.an.tests.eclipselink;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Named;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.an.tests.eclipselink.H2MultiDataSourcesSpringTest.MultiDataSourcesTestConfiguration;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes=MultiDataSourcesTestConfiguration.class)
public class H2MultiDataSourcesSpringTest {

	private static File dir = new File("src/test/data/dbs");

//	@Configuration
	// @EnableAutoConfiguration() ... implies DataSourceAutoConfiguration + many others
	// @Import(DataSourceAutoConfiguration.class)
	public static class MultiDataSourcesTestConfiguration {
		@Bean
		public DataSource dataSource1() {
			String jdbcUrl = "jdbc:h2:" + dir.getAbsolutePath() + "/db1";
	        return JdbcConnectionPool.create(jdbcUrl, "sa", "sa");
		}
		@Bean
		public DataSource dataSource2() {
			String jdbcUrl = "jdbc:h2:" + dir.getAbsolutePath() + "/db2";
	        return JdbcConnectionPool.create(jdbcUrl, "sa", "sa");
		}
	}
	
	@Autowired @Named("dataSource1")
	private DataSource dataSource1;

	@Autowired @Named("dataSource2")
	private DataSource dataSource2;

	@Test
	public void testDataSources() throws Exception {
		Assert.assertNotSame(dataSource1, dataSource2);
		checkDataSource(dataSource1);
		checkDataSource(dataSource2);
	}
	private void checkDataSource(DataSource ds) throws SQLException {
        try (Connection cx = ds.getConnection()) {
        	ResultSet rs = cx.prepareStatement("select 1").executeQuery();
        	if (rs.next()) {
        		int check = rs.getInt(1);
        		System.out.println("OK " + check);
        	}
        }
	}

	
}
