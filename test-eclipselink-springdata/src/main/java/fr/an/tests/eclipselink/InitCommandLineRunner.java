package fr.an.tests.eclipselink;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import fr.an.tests.eclipselink.domain.Hotel;
import fr.an.tests.eclipselink.service.DynQueryService;
import fr.an.tests.eclipselink.service.DynQueryService.HotelSpecification;
import lombok.Data;

@Component
public class InitCommandLineRunner implements CommandLineRunner {
	
	private static final Logger LOG = LoggerFactory.getLogger(InitCommandLineRunner.class);
	
	@Autowired
	private DynQueryService dynQueryService;
	
	@Override
	public void run(String... args) throws Exception {
		HotelSpecification spec0 = new HotelSpecification().nameLike("%ilton%");
		HotelSpecification spec1 = new HotelSpecification().nameLike("%ilton%").cityNameLike("Barcelon%");
		
		List<Hotel> res0 = dynQueryService.findByQuery(spec0);
		// SELECT ID, ADDRESS, NAME, ZIP, CITY_ID FROM HOTEL WHERE NAME LIKE ?
		List<Hotel> res1 = dynQueryService.findByQuery(spec1);
		// SELECT t0.ID, t0.ADDRESS, t0.NAME, t0.ZIP, t0.CITY_ID FROM HOTEL t0, CITY t1 WHERE ((t0.NAME LIKE ? AND t1.NAME LIKE ?) AND (t1.ID = t0.CITY_ID))
//		if (res0.size() != 2 || res1.size() != 1) throw new IllegalStateException();
//		LOG.info("done findByQuery " + res0.size() + " " + res1.size());

		List<Hotel> resQdsl0 = dynQueryService.findByQueryDsl(spec0);
		// SELECT ID, ADDRESS, NAME, ZIP, CITY_ID FROM HOTEL WHERE NAME LIKE '%ilton%' ESCAPE '!'
		List<Hotel> resQdsl1 = dynQueryService.findByQueryDsl(spec1);
		// SELECT t0.ID, t0.ADDRESS, t0.NAME, t0.ZIP, t0.CITY_ID FROM HOTEL t0, CITY t1 WHERE ((t0.NAME LIKE '%ilton%' ESCAPE '!' AND t1.NAME LIKE 'Barcelon%' ESCAPE '!') AND (t1.ID = t0.CITY_ID))
//		if (resQdsl0.size() != 2 || resQdsl1.size() != 1) throw new IllegalStateException();
//		LOG.info("done findByQueryDsl " + resQdsl0.size() + " " + resQdsl1.size());

		List<Hotel> resQdslBV0 = dynQueryService.findByQueryDslBindParams(spec0);
		// SELECT ID, ADDRESS, NAME, ZIP, CITY_ID FROM HOTEL WHERE NAME LIKE '%ilton%' ESCAPE '!'
		List<Hotel> resQdslBV1 = dynQueryService.findByQueryDslBindParams(spec1);
		// SELECT t0.ID, t0.ADDRESS, t0.NAME, t0.ZIP, t0.CITY_ID FROM HOTEL t0, CITY t1 WHERE ((t0.NAME LIKE '%ilton%' ESCAPE '!' AND t1.NAME LIKE 'Barcelon%' ESCAPE '!') AND (t1.ID = t0.CITY_ID))
//		if (resQdslBV0.size() != 2 || resQdslBV1.size() != 1) throw new IllegalStateException();
//		LOG.info("done findByQueryDsl " + resQdslBV0.size() + " " + resQdslBV1.size());
	}

	
	@Inject
	private DataSource dataSource;
	
	public void sample(DataSource dataSource) throws Exception {
		Connection conn = dataSource.getConnection();
		try {
			conn.setAutoCommit(false);
			
			// ** do work with connection **
			
			conn.commit();
		} catch(Exception ex) {
			conn.rollback();
		} finally {
			conn.close();
		}
	}
	
	public void sample2(DataSource dataSource) throws Exception {
		try (Connection conn = dataSource.getConnection()) {
			
			// ** do work with connection **
			
		}
	}
	
	
	@Inject 
	private JdbcTemplate jdbcTemplate; // = new JdbcTemplate(dataSource)
	
	@Transactional
	public Object doWithConnection() throws Exception {
		Object res = jdbcTemplate.execute(new ConnectionCallback<Object>() {
			@Override
			public Object doInConnection(Connection con) throws SQLException, DataAccessException {
				
				// ** do work with connection **
				
				return null;
			}
		});// Pooled connection "closed" => still locked to Thread-Local Transaction
		
		// same with jdk8 lambda (+ explicit type-checking for ambiguous overload)
		res = jdbcTemplate.execute((Connection conn) -> {
			// same Connection as above!
			
			// ** do work with connection **
			
			return null;
		});// Pooled connection "closed" => still locked to Thread-Local Transaction
		
		return res;
	}// <= commit() @Transactional ==> really commit + re-pool connection
	
	@Data
	public static class Emp {
		private int id;
		private String firstName;
		private String lastName;
		private Date birthDate; 
		private int deptFK;
	}
	
	private void testPreparedStatement(Connection cx) {
		DataSource ds;
	}
	
	
}
