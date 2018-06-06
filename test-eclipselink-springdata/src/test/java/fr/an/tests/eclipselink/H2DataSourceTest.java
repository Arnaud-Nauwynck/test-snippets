package fr.an.tests.eclipselink;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Test;

public class H2DataSourceTest {

	@Test
	public void testDataSource() throws Exception {
		File dir = new File("src/test/data/dbs");
		if (!dir.exists()) {
			dir.mkdirs();
		}		
		String jdbcUrl = "jdbc:h2:" + dir.getAbsolutePath() + "/db1";
		String username = "sa", password = "sa";
		
        JdbcConnectionPool dataSource = JdbcConnectionPool.create(jdbcUrl, username, password);
        
        try (Connection cx = dataSource.getConnection()) {
        	ResultSet rs = cx.prepareStatement("select 1").executeQuery();
        	if (rs.next()) {
        		int check = rs.getInt(1);
        		System.out.println("OK " + check);
        	}
        }
        
        dataSource.dispose();
	}

	
}
