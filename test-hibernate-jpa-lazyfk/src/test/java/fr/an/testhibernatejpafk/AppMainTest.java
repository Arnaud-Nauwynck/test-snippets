package fr.an.testhibernatejpafk;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.an.testhibernatejpafk.dto.DepartmentDTO;
import fr.an.testhibernatejpafk.dto.DepartmentDTO2;
import fr.an.testhibernatejpafk.dto.DepartmentDTO3;
import fr.an.testhibernatejpafk.dto.DepartmentDTO3Bis;
import fr.an.testhibernatejpafk.dto.DepartmentDTO4;
import fr.an.testhibernatejpafk.dto.EmployeeDTO;
import fr.an.testhibernatejpafk.dto.EmployeeDTO2;
import fr.an.testhibernatejpafk.dto.EmployeeDTO3;
import fr.an.testhibernatejpafk.dto.EmployeeDTO3Bis;
import fr.an.testhibernatejpafk.dto.EmployeeDTO4;
import fr.an.testhibernatejpafk.repository.EmployeeRepo;
import fr.an.testhibernatejpafk.service.ApiService;
import fr.an.testhibernatejpafk.service.DataFeederService;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class AppMainTest {

	@Autowired
	private DataFeederService feederService;

	@Autowired
	private ApiService apiService;

	@Autowired
	private EmployeeRepo employeeRepo;

	@Before
	public void feedData( ) {
		if (0 == employeeRepo.count()) {
			log.info("feeding dummy database..");
			feederService.feed();
			log.info(".. done feeding dummy database");
		}
	}
	
	@Test
	public void testFeed() throws Exception {
		feederService.feed();
	}

	@Test
	public void testFindAllDTO() throws Exception {
		List<EmployeeDTO> empDTOs = apiService.listEmployeeDTOs();
		Assert.assertNotNull(empDTOs);
		// => ONLY 1 sql query:
//	  select
//        employee0_.id as id1_1_,
//        employee0_.address as address2_1_,
//        employee0_.department_id as departme3_1_,
//        employee0_.email as email4_1_,
//        employee0_.first_name as first_na5_1_,
//        employee0_.last_name as last_nam6_1_,
//        employee0_.manager_id as manager_7_1_,
//        employee0_.version as version8_1_ 
//    from
//        employee employee0_
		
		EmployeeDTO emp1 = empDTOs.get(0);
		Assert.assertEquals(1, emp1.getId());
		Assert.assertEquals("John", emp1.getFirstName());
		Assert.assertEquals("Smith", emp1.getLastName());
		
		EmployeeDTO dept2Manager = empDTOs.get(1);
		Assert.assertEquals(2, dept2Manager.getId());
		Assert.assertEquals("MngFirstname2", dept2Manager.getFirstName());
		Assert.assertEquals("MngLastname2", dept2Manager.getLastName());
		Assert.assertEquals(2, dept2Manager.getDepartmentId().intValue());
		Assert.assertNull(dept2Manager.getManagerId());
		
		EmployeeDTO dep2Emp2 = empDTOs.get(2);
		Assert.assertEquals(3, dep2Emp2.getId());
		Assert.assertEquals("DeptEmp2Firstname2", dep2Emp2.getFirstName());
		Assert.assertEquals("DeptEmp2Lastname2", dep2Emp2.getLastName());
		Assert.assertEquals(2, dep2Emp2.getDepartmentId().intValue());
		Assert.assertEquals(dept2Manager.getId(), dep2Emp2.getManagerId().intValue());
		
		List<DepartmentDTO> deptDTOs = apiService.listDepartmentDTOs();
		Assert.assertNotNull(deptDTOs);
		// => ONLY 1 sql query:
//	select
//        department0_.id as id1_0_,
//        department0_.dept_manager_id as dept_man5_0_,
//        department0_.manager_id as manager_2_0_,
//        department0_.department_name as departme3_0_,
//        department0_.version as version4_0_ 
//    from
//        department department0_	
		
		DepartmentDTO deptIT = deptDTOs.get(0);
		Assert.assertEquals(1, deptIT.getId());
		Assert.assertEquals("IT", deptIT.getName());
		
		DepartmentDTO dept2 = deptDTOs.get(1);
		Assert.assertEquals(2, dept2.getId());
		Assert.assertEquals("Dept2", dept2.getName());
		Assert.assertEquals(dept2Manager.getId(), dept2.getDeptManagerId().intValue());
		
	}

	@Test
	public void testFindAllDTO2() throws Exception {
		List<EmployeeDTO2> empDTOs = apiService.listEmployeeDTO2s();
		Assert.assertNotNull(empDTOs);
		// => ONLY 1 sql query:
//	  select
//        employee0_.id as id1_1_,
//        employee0_.address as address2_1_,
//        employee0_.department_id as departme3_1_,
//        employee0_.email as email4_1_,
//        employee0_.first_name as first_na5_1_,
//        employee0_.last_name as last_nam6_1_,
//        employee0_.manager_id as manager_7_1_,
//        employee0_.version as version8_1_ 
//    from
//        employee employee0_
		
		EmployeeDTO2 emp1 = empDTOs.get(0);
		Assert.assertEquals(1, emp1.getId());
		Assert.assertEquals("John", emp1.getFirstName());
		Assert.assertEquals("Smith", emp1.getLastName());
		
		EmployeeDTO2 dept2Manager = empDTOs.get(1);
		Assert.assertEquals(2, dept2Manager.getId());
		Assert.assertEquals("MngFirstname2", dept2Manager.getFirstName());
		Assert.assertEquals("MngLastname2", dept2Manager.getLastName());
		Assert.assertEquals(2, dept2Manager.getDepartment().getId());
		Assert.assertNull(dept2Manager.getManager());
		
		EmployeeDTO2 dep2Emp2 = empDTOs.get(2);
		Assert.assertEquals(3, dep2Emp2.getId());
		Assert.assertEquals("DeptEmp2Firstname2", dep2Emp2.getFirstName());
		Assert.assertEquals("DeptEmp2Lastname2", dep2Emp2.getLastName());
		Assert.assertEquals(2, dep2Emp2.getDepartment().getId());
		Assert.assertEquals(dept2Manager.getId(), dep2Emp2.getManager().getId());
		
		List<DepartmentDTO2> deptDTOs = apiService.listDepartmentDTO2s();
		Assert.assertNotNull(deptDTOs);
		// => ONLY 1 sql query:
//	select
//        department0_.id as id1_0_,
//        department0_.dept_manager_id as dept_man5_0_,
//        department0_.manager_id as manager_2_0_,
//        department0_.department_name as departme3_0_,
//        department0_.version as version4_0_ 
//    from
//        department department0_	
		
		DepartmentDTO2 deptIT = deptDTOs.get(0);
		Assert.assertEquals(1, deptIT.getId());
		Assert.assertEquals("IT", deptIT.getName());
		
		DepartmentDTO2 dept2 = deptDTOs.get(1);
		Assert.assertEquals(2, dept2.getId());
		Assert.assertEquals("Dept2", dept2.getName());
		Assert.assertEquals(dept2Manager.getId(), dept2.getDeptManager().getId());
		
	}

	@Test
	public void testFindAllEmployeeDTO3() throws Exception {
		List<EmployeeDTO3> empDTOs = apiService.listEmployeeDTO3s();
		Assert.assertNotNull(empDTOs);
		// N+1 SQL Queries!!
		
		EmployeeDTO3 emp1 = empDTOs.get(0);
		Assert.assertEquals(1, emp1.getId());
		Assert.assertEquals("John", emp1.getFirstName());
		Assert.assertEquals("Smith", emp1.getLastName());
		
		EmployeeDTO3 dept2Manager = empDTOs.get(1);
		Assert.assertEquals(2, dept2Manager.getId());
		Assert.assertEquals("MngFirstname2", dept2Manager.getFirstName());
		Assert.assertEquals("MngLastname2", dept2Manager.getLastName());
		Assert.assertEquals(2, dept2Manager.getDepartment().getId());
		Assert.assertEquals("Dept2", dept2Manager.getDepartment().getName());
		Assert.assertNull(dept2Manager.getManager());
		
		EmployeeDTO3 dep2Emp2 = empDTOs.get(2);
		Assert.assertEquals(3, dep2Emp2.getId());
		Assert.assertEquals("DeptEmp2Firstname2", dep2Emp2.getFirstName());
		Assert.assertEquals("DeptEmp2Lastname2", dep2Emp2.getLastName());
		Assert.assertEquals(2, dep2Emp2.getDepartment().getId());
		Assert.assertEquals("Dept2", dep2Emp2.getDepartment().getName());
		Assert.assertEquals(dept2Manager.getId(), dep2Emp2.getManager().getId());
		Assert.assertEquals(dept2Manager.getFirstName(), dep2Emp2.getManager().getFirstName());
		Assert.assertEquals(dept2Manager.getLastName(), dep2Emp2.getManager().getLastName());
	}
	
	@Test
	public void testFindAllDepartmentDTO3() throws Exception {
		List<DepartmentDTO3> deptDTOs = apiService.listDepartmentDTO3s();
		Assert.assertNotNull(deptDTOs);
		// N+1 1 SQL Queries!!
		
		DepartmentDTO3 deptIT = deptDTOs.get(0);
		Assert.assertEquals(1, deptIT.getId());
		Assert.assertEquals("IT", deptIT.getName());
		
		DepartmentDTO3 dept2 = deptDTOs.get(1);
		Assert.assertEquals(2, dept2.getId());
		Assert.assertEquals("Dept2", dept2.getName());
		Assert.assertEquals(2, dept2.getDeptManager().getId());
		Assert.assertEquals("MngFirstname2", dept2.getDeptManager().getFirstName());
		Assert.assertEquals("MngLastname2", dept2.getDeptManager().getLastName());
		
	}


	@Test
	public void testFindAllEmployeeDTO3Bis_usingJoins() throws Exception {
		List<EmployeeDTO3Bis> empDTOs = apiService.listEmployeeDTO3Bis_usingJoins();
		Assert.assertNotNull(empDTOs);
		// ONLY 1 SQL Query with JOIN!!
		// select employee0_.id as col_0_0_, employee0_.version as col_1_0_, employee0_.first_name as col_2_0_, employee0_.last_name as col_3_0_, employee0_.email as col_4_0_, employee0_.address as col_5_0_, department1_.id as col_6_0_, department1_.department_name as col_7_0_, employee2_.id as col_8_0_, employee2_.first_name as col_9_0_, employee2_.last_name as col_10_0_, employee2_.email as col_11_0_ 
		// from employee employee0_ 
		// LEFT OUTER JOIN department department1_ on employee0_.department_id=department1_.id 
		// LEFT OUTER JOIN employee employee2_ on employee0_.manager_id=employee2_.id

		EmployeeDTO3Bis emp1 = findFirst(empDTOs, x -> x.getId() == 1);
		Assert.assertEquals(1, emp1.getId());
		Assert.assertEquals("John", emp1.getFirstName());
		Assert.assertEquals("Smith", emp1.getLastName());
		
		EmployeeDTO3Bis dept2Manager = findFirst(empDTOs, x -> x.getId() == 2);
		Assert.assertEquals(2, dept2Manager.getId());
		Assert.assertEquals("MngFirstname2", dept2Manager.getFirstName());
		Assert.assertEquals("MngLastname2", dept2Manager.getLastName());
		Assert.assertEquals(2, dept2Manager.getDepartment().getId());
		Assert.assertEquals("Dept2", dept2Manager.getDepartment().getName());
		Assert.assertNull(dept2Manager.getManager());
		
		EmployeeDTO3Bis dep2Emp2 = findFirst(empDTOs, x -> x.getId() == 3);
		Assert.assertEquals(3, dep2Emp2.getId());
		Assert.assertEquals("DeptEmp2Firstname2", dep2Emp2.getFirstName());
		Assert.assertEquals("DeptEmp2Lastname2", dep2Emp2.getLastName());
		Assert.assertEquals(2, dep2Emp2.getDepartment().getId());
		Assert.assertEquals("Dept2", dep2Emp2.getDepartment().getName());
		Assert.assertEquals(dept2Manager.getId(), dep2Emp2.getManager().getId());
		Assert.assertEquals(dept2Manager.getFirstName(), dep2Emp2.getManager().getFirstName());
		Assert.assertEquals(dept2Manager.getLastName(), dep2Emp2.getManager().getLastName());
	}
	
	@Test
	public void testFindAllDepartmentDTO3Bis_usingJoins() throws Exception {
		List<DepartmentDTO3Bis> deptDTOs = apiService.listDepartmentDTO3Bis_usingJoins();
		Assert.assertNotNull(deptDTOs);
		// ONLY 1 SQL Query with JOIN!!
		// select department0_.id as col_0_0_, department0_.version as col_1_0_, department0_.department_name as col_2_0_, employee1_.id as col_3_0_, employee1_.first_name as col_4_0_, employee1_.last_name as col_5_0_, employee1_.email as col_6_0_ 
		// from department department0_ 
		// LEFT OUTER JOIN employee employee1_ on department0_.dept_manager_id=employee1_.id

		DepartmentDTO3Bis deptIT = findFirst(deptDTOs, x -> x.getId() == 1);
		Assert.assertEquals(1, deptIT.getId());
		Assert.assertEquals("IT", deptIT.getName());
		
		DepartmentDTO3Bis dept2 = findFirst(deptDTOs, x -> x.getId() == 2);
		Assert.assertEquals(2, dept2.getId());
		Assert.assertEquals("Dept2", dept2.getName());
		Assert.assertEquals(2, dept2.getDeptManager().getId());
		Assert.assertEquals("MngFirstname2", dept2.getDeptManager().getFirstName());
		Assert.assertEquals("MngLastname2", dept2.getDeptManager().getLastName());
		
	}


	
	@Test
	public void testFindAllEmployeeDTO4() throws Exception {
		List<EmployeeDTO4> empDTOs = apiService.listEmployeeDTO4s();
		Assert.assertNotNull(empDTOs);
		// N+1 SQL Queries!!
		// select * from employee
		// .. select * from department where id=?
		// .. select * from department where id=?
		
		EmployeeDTO4 emp1 = empDTOs.get(0);
		Assert.assertEquals(1, emp1.getId());
		Assert.assertEquals("John", emp1.getFirstName());
		Assert.assertEquals("Smith", emp1.getLastName());
		
		EmployeeDTO4 dept2Manager = empDTOs.get(1);
		Assert.assertEquals(2, dept2Manager.getId());
		Assert.assertEquals("MngFirstname2", dept2Manager.getFirstName());
		Assert.assertEquals("MngLastname2", dept2Manager.getLastName());
		Assert.assertEquals(2, dept2Manager.getDepartment().getId());
		Assert.assertNull(dept2Manager.getManager());
		
		EmployeeDTO4 dep2Emp2 = empDTOs.get(2);
		Assert.assertEquals(3, dep2Emp2.getId());
		Assert.assertEquals("DeptEmp2Firstname2", dep2Emp2.getFirstName());
		Assert.assertEquals("DeptEmp2Lastname2", dep2Emp2.getLastName());
		Assert.assertEquals(2, dep2Emp2.getDepartment().getId());
		Assert.assertEquals(dept2Manager.getId(), dep2Emp2.getManager().getId());
	}
	
	@Test
	public void testFindAllDepartmentDTO4() throws Exception {
		List<DepartmentDTO4> deptDTOs = apiService.listDepartmentDTO4s();
		Assert.assertNotNull(deptDTOs);
		// N+1 SQL Queries!!
		// select * from department
		// .. select * from employee where id=?
		// .. select * from employee where id=?

		DepartmentDTO4 deptIT = deptDTOs.get(0);
		Assert.assertEquals(1, deptIT.getId());
		Assert.assertEquals("IT", deptIT.getName());
		
		DepartmentDTO4 dept2 = deptDTOs.get(1);
		Assert.assertEquals(2, dept2.getId());
		Assert.assertEquals("Dept2", dept2.getName());
		Assert.assertEquals(2, dept2.getDeptManager().getId());
		Assert.assertEquals("MngFirstname2", dept2.getDeptManager().getFirstName());
		Assert.assertEquals("MngLastname2", dept2.getDeptManager().getLastName());
	}

	
	public static <T> T findFirst(Collection<T> ls, Predicate<T> pred) {
		for(T elt : ls) {
			if (pred.test(elt)) {
				return elt;
			}
		}
		return null;
	}
	
}
