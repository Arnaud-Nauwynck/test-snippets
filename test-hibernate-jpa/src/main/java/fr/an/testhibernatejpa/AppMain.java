package fr.an.testhibernatejpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import fr.an.testhibernatejpa.domain.Department;
import fr.an.testhibernatejpa.domain.Employee;

public class AppMain {

	EntityManager em;
	EntityTransaction tx;
	int empId1;
	int deptId1;
	
	public static void main(String[] args) throws Exception {
		new AppMain().run();
	}
	
	public void run() throws Exception {
		long start = System.currentTimeMillis();
		
		HibernateJpaHelper jpaHelper = new HibernateJpaHelper();

		jpaHelper.setUp();
		
		this.em = jpaHelper.createEM();
		this.tx = em.getTransaction();
		long timeMillis = System.currentTimeMillis() - start;
		
		runXA(() -> test1());
		runXA(() -> test2());

		em.close();

		long testTimeMillis = System.currentTimeMillis() - start;
		System.out.println("Time to bootstrap: " + timeMillis + "ms .. to test: " + testTimeMillis + "ms");
		// => Time to bootstratp: 1332ms .. to test: 1371ms
		System.exit(0);
	}

	private void runXA(Runnable runnable) {
		tx.begin();
		try {
			runnable.run();
			tx.commit();
		} catch(RuntimeException ex) {
			tx.rollback();
			throw ex;
		}
	}
	
	public void test1() {
		Employee emp = new Employee();
		emp.setFirstName("John");
		emp.setLastName("Smith");
		
		Department dept = new Department();
		dept.setName("IT");


		em.persist(emp);
		this.empId1 = emp.getId();
		
		em.persist(dept);
		this.deptId1 = dept.getId();

		dept.getEmployees().add(emp); 
		assert emp.getDepartment() == null; // need explicit manage inverse!
		emp.setDepartment(dept);
		
		List<Employee> deptEmployees = dept.getEmployees();
		if (deptEmployees.size() != 1 || deptEmployees.get(0) != emp) {
			throw new IllegalStateException();
		}

	}
	
	public void test2() {
		Employee emp1 = em.find(Employee.class, this.empId1);
		Department dept1 = em.find(Department.class, this.deptId1);
		
		if (emp1.getDepartment() != dept1) {
			throw new IllegalStateException();
		}
		List<Employee> deptEmployees = dept1.getEmployees();
		if (deptEmployees.size() != 1 || deptEmployees.get(0) != emp1) {
			throw new IllegalStateException();
		}
		
	}
	
}
