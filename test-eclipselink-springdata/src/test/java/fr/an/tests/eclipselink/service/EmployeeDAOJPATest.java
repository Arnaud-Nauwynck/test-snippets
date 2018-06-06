package fr.an.tests.eclipselink.service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.an.testjpa.postgresql.PgJdbcApp;
import fr.an.tests.eclipselink.domain.Employee;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PgJdbcApp.class)
@Transactional @Rollback
public class EmployeeDAOJPATest {

	@Autowired
	private EntityManager em;
	
	@Test
	public void testFindById() {
		int id = 2;
		Employee emp = em.find(Employee.class, id); 
		//=> "SELECT employee_id, EMAIL, first_name, last_name, VERSION 
		//   FROM employees WHERE (employee_id = ?)"
		Assert.assertEquals(id, emp.getId());
	}
	
	@Test @Rollback
	public void testCreate() {
		Employee emp = new Employee();
		emp.setFirstName("John");
		emp.setLastName("Smith");
		emp.setEmail("jown.smith@company.com");
		
		em.persist(emp);  // => "select nextval('employees_seq')"
		
		Assert.assertTrue(emp.getId() > 0); 
		em.flush(); // => execute but no commit.. "INSERT INTO employees (ID, ADDRESS, EMAIL, FIRSTNAME, LASTNAME, VERSION, DEPARTMENT_ID, MANAGER_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
	} //<= rollback

	@Test @Rollback
	public void testUpdate() {
		int id = 2;
		Employee emp = em.find(Employee.class, id);
		emp.setEmail("test-" + emp.getEmail());
		em.flush(); // => execute but no commit.. "UPDATE employees SET EMAIL = ?, VERSION = ? WHERE ((employee_id = ?) AND (VERSION = ?))"
	} //<= rollback
	
	@Test @Rollback
	public void testDelete() {
		int id = 6;
		Employee emp = em.find(Employee.class, id);
		em.remove(emp);
		em.flush(); // => execute but no commit.. "DELETE FROM employees WHERE ((employee_id = ?) AND (VERSION = ?))"
	} //<= rollback
	
}
