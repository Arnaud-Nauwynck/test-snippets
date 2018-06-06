package fr.an.tests.eclipselink;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.an.tests.eclipselink.H2DataSourceSpringTest.DataSourceTestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DataSourceTestConfiguration.class)
public class H2DataSourceSpringTest {

	@Configuration
	// @EnableAutoConfiguration() ... implies DataSourceAutoConfiguration + many others
	@Import(DataSourceAutoConfiguration.class) 
	// cf breakpoint in DataSourceAutoConfiguration$NonEmbeddedConfiguration.dataSource() 
	// => read file conf/application.yml, use (or default) properties: 
	// spring:
	//   datasource:
	//     url: 
	//     username:
	//     password:
	public static class DataSourceTestConfiguration {
	}
	
	@Autowired
	private DataSource dataSource;
	
	@Test
	public void testDataSource() throws Exception {
		Connection cx = dataSource.getConnection();
		try {
        	
        	try (PreparedStatement stmt = cx.prepareStatement("select 1")) {
				try (ResultSet rs = stmt.executeQuery()) {
					
					if (rs.next()) {
						Assert.assertEquals(1, rs.getInt(1));
					}
		        	
				} // <= close rs
        	} // <= close stmt
        	
        	cx.commit();
        } catch(Exception ex) {
        	cx.rollback();
        } finally {
        	cx.close(); // close cx after commit/rollback
        }
	}
	
}
