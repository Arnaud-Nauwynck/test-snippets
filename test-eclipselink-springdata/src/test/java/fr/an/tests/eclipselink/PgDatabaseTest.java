package fr.an.tests.eclipselink;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;

import fr.an.tests.eclipselink.domain.Employee;
import lombok.Data;

public class PgDatabaseTest {

	private static DataSource dataSource = createDS();
	private static DataSource createDS() {
		org.postgresql.ds.PGPoolingDataSource ds = new org.postgresql.ds.PGPoolingDataSource();
		ds.setUrl("jdbc:postgresql://localhost:5432/empdept");
		ds.setUser("postgres");
		ds.setPassword("pg");
		return ds;
	}
	
	@Test
	public void testDataSource() throws Exception {
		Connection cx = dataSource.getConnection();
		cx.setAutoCommit(false);
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
	
	@Test
	public void testSelect() throws SQLException {
		final int empId = 2;
		Employee emp = executeXAWithConnection((Connection cx) -> {
			String sql = "select e.employee_id, " + //
					" from employees e " + // 
					"where e.id = ?";
			PreparedStatement stmt = cx.prepareStatement(sql);
			stmt.setInt(1, empId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Employee e = new Employee();
					e.setId(rs.getInt("employee_id"));
					
					return e;
				}
				return null;
			}
		});
		Assert.assertNotNull(emp);
		Assert.assertEquals(empId, emp.getId());
	}
	
	
	@Test
	public void testSelectEmployeeById2() throws SQLException {
		Connection cx = dataSource.getConnection();
		cx.setAutoCommit(false);
		try {
			//{{{
			final int empId = 2;
			String sql = "select e.employee_id, e.first_name, e.last_name, e.email " + //
					"from employees e " + // 
					"where e.employee_id = ?";
			PreparedStatement stmt = cx.prepareStatement(sql);
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
			cx.commit();
			Assert.assertNotNull(emp);
			Assert.assertEquals(empId, emp.getId());
	    	// }}}}
	    } catch(Exception ex) {
	    	cx.rollback();
	    	throw new RuntimeException("Failed ..rolleback", ex);
	    } finally {
	    	cx.close(); // close cx after commit/rollback
	    }
	}

	/**
	 * DTO (Data Transfer Object) for Employee
	 * Serializable data representation of a Row
	 * agnostic of any framework
	 */
	@Data // lombok getter, setter
	public static class EmployeeDTO implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private int id; // Primary Key
		private int version; // field for Optimistic lock
		
		private String firstName;
		private String lastName;
		private String email;
		private String address;
		private Date hireDate;
		private double salary;
		
		// relationships fields ... lazy loaded!, replace by ForeignKey id
		// private Department department;  
		private int departmentId;
		// private Employee manager;
		private int managerId;
	}
	
	@Test
	public void testFindEmployeeById() throws SQLException {
		executeRollbackVoid(cx -> {
			final int empId = 2;
			String sql = "SELECT e.employee_id," + //
					" e.first_name, e.last_name,"+ // 
					" e.email, e.address," + // 
					" e.hire_date, e.salary," + //
					" e.department_id, e.manager_id " + //
					"FROM employees e " + // 
					"WHERE e.employee_id = ?";
			try (PreparedStatement stmt = cx.prepareStatement(sql)) {
				stmt.setInt(1, empId);
				EmployeeDTO emp = null;
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						emp = new EmployeeDTO();
						emp.setId(rs.getInt("employee_id"));
						emp.setFirstName(rs.getString("first_name"));
						emp.setLastName(rs.getString("last_name"));
						emp.setEmail(rs.getString("email"));
						emp.setAddress(rs.getString("address"));
						emp.setHireDate(rs.getDate("hire_date"));
						emp.setSalary(rs.getDouble("salary"));
						emp.setDepartmentId(rs.getInt("department_id"));
						emp.setManagerId(rs.getInt("manager_id"));
					}
				}
				Assert.assertNotNull(emp);
				Assert.assertEquals(empId, emp.getId());
			}
		});
	}
	
	
	@Test
	public void testCreate() throws SQLException {
		final EmployeeDTO emp = new EmployeeDTO();
		// emp.setId()?? don't .. use database sequence
		emp.setFirstName("John");
		emp.setLastName("Smith");
		emp.setEmail("jown.smith@company.com");
		emp.setHireDate(new Date());
		emp.setSalary(60000);
		emp.setDepartmentId(3);
		emp.setManagerId(4);
		
		executeRollbackVoid(cx -> {
			String sql = "INSERT INTO employees (employee_id," + //
					" first_name, last_name,"+ // 
					" email, address," + // 
					" hire_date, salary," + //
					" department_id, manager_id) " + //
					"VALUES (nextval('employees_seq'),?,?,?,?,?,?,?,?)";
			try (PreparedStatement stmt = cx.prepareStatement(sql)) {
				int i = 1;
				stmt.setString(i++, emp.getFirstName());
				stmt.setString(i++, emp.getLastName());
				stmt.setString(i++, emp.getEmail());
				stmt.setString(i++, emp.getAddress());
				stmt.setDate(i++, new java.sql.Date(emp.getHireDate().getTime()));
				stmt.setDouble(i++, emp.getSalary());
				stmt.setInt(i++, emp.getDepartmentId());
				stmt.setInt(i++, emp.getManagerId());
				
				int updateCount = stmt.executeUpdate();
				Assert.assertEquals(1, updateCount);
			}
		});//even if rollbacked... sequence is incremented each time!
	}

	@Test
	public void testUpdate_overwrite_no_OptimisticLock() throws SQLException {
		final int empId = 4;
		final EmployeeDTO emp = createEmployeeJohnSmith(empId);
		
		executeRollbackVoid(cx -> {
			String sql = "UPDATE employees " + // 
					"SET first_name=?, last_name=?,"+ // 
					" email=?, address=?," + // 
					" hire_date=?, salary=?," + //
					" department_id=?, manager_id=? " + //
					"WHERE employee_id=?";
			try (PreparedStatement stmt = cx.prepareStatement(sql)) {
				int i = 1;
				stmt.setString(i++, emp.getFirstName());
				stmt.setString(i++, emp.getLastName());
				stmt.setString(i++, emp.getEmail());
				stmt.setString(i++, emp.getAddress());
				stmt.setDate(i++, new java.sql.Date(emp.getHireDate().getTime()));
				stmt.setDouble(i++, emp.getSalary());
				stmt.setInt(i++, emp.getDepartmentId());
				stmt.setInt(i++, emp.getManagerId());

				stmt.setInt(i++, emp.getId());

				int updateCount = stmt.executeUpdate();
				Assert.assertEquals(1, updateCount);
			}
		});
	}
	
	@Test
	public void testUpdate_with_OptimisticLock() throws SQLException {
		final int empId = 4;
		final int expectedCurrentVersion = 503;
		final EmployeeDTO emp = createEmployeeJohnSmith(empId);
		
		executeRollbackVoid(cx -> {
			String sql = "UPDATE employees " + // 
					"SET first_name=?, last_name=?,"+ // 
					" email=?, address=?," + // 
					" hire_date=?, salary=?," + //
					" department_id=?, manager_id=?, " + //
					" version=? "+ //
					"WHERE employee_id=? AND version=?";
			try (PreparedStatement stmt = cx.prepareStatement(sql)) {
				int i = 1;
				stmt.setString(i++, emp.getFirstName());
				stmt.setString(i++, emp.getLastName());
				stmt.setString(i++, emp.getEmail());
				stmt.setString(i++, emp.getAddress());
				stmt.setDate(i++, new java.sql.Date(emp.getHireDate().getTime()));
				stmt.setDouble(i++, emp.getSalary());
				stmt.setInt(i++, emp.getDepartmentId());
				stmt.setInt(i++, emp.getManagerId());

				int incrementedVersion = expectedCurrentVersion + 1;
				stmt.setInt(i++, incrementedVersion);
				stmt.setInt(i++, emp.getId());
				stmt.setInt(i++, expectedCurrentVersion);

				int updateCount = stmt.executeUpdate();
				if (updateCount != 1) {
					throw new OptimisticLockException("attemp to overwriting Employee " + emp.getId() +
							" using stale version:" + expectedCurrentVersion + " - Please refresh and retry");
				}
				Assert.assertEquals(1, updateCount);
			}
		});
	}

	private EmployeeDTO createEmployeeJohnSmith(final int empId) {
		final EmployeeDTO emp = new EmployeeDTO();
		emp.setId(empId);
		emp.setFirstName("John");
		emp.setLastName("Smith");
		emp.setEmail("jown.smith@company.com");
		emp.setHireDate(new Date());
		emp.setSalary(60000);
		emp.setDepartmentId(3);
		emp.setManagerId(4);
		return emp;
	}
	
	@Test
	public void testDelete() throws SQLException {
		final int empId = 4;
		
		executeRollbackVoid(cx -> {
			String sql = "DELETE FROM employees " + // 
					"WHERE employee_id=?";
			try (PreparedStatement stmt = cx.prepareStatement(sql)) {
				stmt.setInt(1, empId);

				int updateCount = stmt.executeUpdate();
				if (updateCount != 1) {
					throw new EntityNotFoundException();
				}
				Assert.assertEquals(1, updateCount);
			}
		});
	}
	
//	@Test @Rollback
//	public void testUpdate() {
//		int id = 2;
//		Employee emp = em.find(Employee.class, id);
//		emp.setEmail("test-" + emp.getEmail());
//		em.flush(); // => execute but no commit.. "UPDATE employees SET EMAIL = ?, VERSION = ? WHERE ((employee_id = ?) AND (VERSION = ?))"
//	} //<= rollback
//	
//	@Test @Rollback
//	public void testDelete() {
//		int id = 6;
//		Employee emp = em.find(Employee.class, id);
//		em.remove(emp);
//		em.flush(); // => execute but no commit.. "DELETE FROM employees WHERE ((employee_id = ?) AND (VERSION = ?))"
//	} //<= rollback
	
	
	
	
	
	@Test
	public void testExecuteRollback() throws SQLException {
		executeRollbackVoid(cx -> {
			String sql = "select 'Hello'";
			try(PreparedStatement stmt = 
					cx.prepareStatement(sql)) {
				ResultSet rs = stmt.executeQuery();
				String res = rs.getString(1);
				Assert.assertEquals("Hello", res);
			}			
		});
	}
	
	
	@Test
	public void testExecuteRollback_before() throws SQLException {		
		Connection cx = dataSource.getConnection();
		cx.setAutoCommit(false);
		try {
			String sql = "select 'Hello'";
			try(PreparedStatement stmt = 
					cx.prepareStatement(sql)) {				
				ResultSet rs = stmt.executeQuery();
				String res = rs.getString(1);
				Assert.assertEquals("Hello", res);
			}			
			
	    	cx.rollback();
	    } catch(Exception ex) {
	    	cx.rollback();
	    	throw new RuntimeException("Failed ..rolleback", ex);
	    } finally {
	    	cx.close();
	    }
	}
	
	@FunctionalInterface
	public static interface CxVoidCallback {
		public void executeWithConnection(Connection cx) throws SQLException;
	}
	
	private void executeRollbackVoid(CxVoidCallback callback) throws SQLException {
		Connection cx = dataSource.getConnection();
		cx.setAutoCommit(false);
		try {
			// ** execute **
			callback.executeWithConnection(cx);
			
	    	cx.rollback();
	    } catch(Exception ex) {
	    	cx.rollback();
	    	throw new RuntimeException("Failed ..rolleback", ex);
	    } finally {
	    	cx.close(); // close cx after commit/rollback
	    }
	}	
	
	
	
	@FunctionalInterface
	public static interface CxCallback<T> {
		public T executeWithConnection(Connection cx) throws SQLException;
	}
	
	private <T> T executeXAWithConnection(CxCallback<T> callback) throws SQLException {
		Connection cx = dataSource.getConnection();
		cx.setAutoCommit(false);
		try {
			// ** execute **
			T res = callback.executeWithConnection(cx);
			
	    	cx.commit();
	    	return res;
	    } catch(Exception ex) {
	    	cx.rollback();
	    	throw new RuntimeException("Failed ..rolleback", ex);
	    } finally {
	    	cx.close(); // close cx after commit/rollback
	    }
	}	
	
}
