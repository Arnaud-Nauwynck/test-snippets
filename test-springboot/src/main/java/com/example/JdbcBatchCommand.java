package com.example;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class JdbcBatchCommand implements CommandLineRunner {

	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	@Override
	public void run(String... args) throws Exception {
		jdbcTemplate.execute("select 1");
		
		String msg = jdbcTemplate.execute("select 'Hello' as msg", (PreparedStatement pstmt) -> {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("msg");
			}
			return null;
		});
		assert "Hello".equals(msg);
	} // after this "}" line, spring will commit (or rollback ACID Transaction)

}
