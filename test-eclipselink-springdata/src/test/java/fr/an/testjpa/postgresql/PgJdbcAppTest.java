package fr.an.testjpa.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.an.tests.eclipselink.domain.Employee;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PgJdbcApp.class)
public class PgJdbcAppTest {
	
	@Autowired
	private DataSource dataSource;
	
	@Test
	public void testDataSource() throws SQLException {
		Assert.assertNotNull(dataSource);
		try (Connection cx = dataSource.getConnection()) {
			Assert.assertNotNull(cx);
			
			try (PreparedStatement stmt = cx.prepareStatement("select 'Hello'")) {
				ResultSet rs = stmt.executeQuery();
				rs.next();
				String res = rs.getString(1);
				Assert.assertEquals("Hello", res);
			}
		}
	}
	
	
	
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
	
	@Test
	public void testJdbcTemplate() throws Exception {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		final int empId = 2;
		String sql = "select e.employee_id, e.first_name, e.last_name, e.email " + //
				"from employees e " + // 
				"where e.employee_id = ?";
		Employee e = jdbcTemplate.execute(sql, (PreparedStatement stmt) -> {
			stmt.setInt(1, empId);
			Employee emp = null;
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					emp = new Employee();
					emp.setId(rs.getInt("employee_id"));
					emp.setFirstName(rs.getString("first_name"));
					emp.setLastName(rs.getString("last_name"));
					emp.setEmail(rs.getString("email"));
				}
			}
			return emp;
		});
		Assert.assertEquals(empId,  e.getId());
	}
	
}
