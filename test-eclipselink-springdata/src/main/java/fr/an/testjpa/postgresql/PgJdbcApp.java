package fr.an.testjpa.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;

import fr.an.tests.eclipselink.domain.Employee;

@ComponentScan(basePackages="fr.an")
@SpringBootApplication
public class PgJdbcApp implements CommandLineRunner {

	@Autowired
	private DataSource dataSource;
	
	public static void main(String[] args) {
		new SpringApplication(PgJdbcApp.class).run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		try (Connection cx = dataSource.getConnection()) {
			try (PreparedStatement stmt = cx.prepareStatement("select 'Hello'")) {
				ResultSet rs = stmt.executeQuery();
				rs.next();
				String res = rs.getString(1);
				Assert.assertEquals("Hello", res);
			}
		}
	}
	
	
	
	
//	implements CommandLineRunner 
//	@Override
	public void run2(String... args) throws Exception {
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
