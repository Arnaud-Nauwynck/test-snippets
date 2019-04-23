package fr.an.testhibernatejpafk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.an.testhibernatejpafk.domain.Department;
import fr.an.testhibernatejpafk.domain.Employee;
import fr.an.testhibernatejpafk.repository.DepartmentRepo;
import fr.an.testhibernatejpafk.repository.EmployeeRepo;

@Component
@Transactional
public class DataFeederService {

	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	private DepartmentRepo departmentRepo;

	@Autowired
	private XaHelper xaHelper;
		
	public void feed() {
		xaHelper.xanew(() -> feed1());
	}

	
	public void feed1() {
		Employee emp1 = new Employee();
		emp1.setFirstName("John");
		emp1.setLastName("Smith");
		emp1 = employeeRepo.save(emp1);

		Department dept1 = new Department();
		dept1.setName("IT");
		departmentRepo.save(dept1);

		for(int d = 2; d < 50; d++) {
			Department dept = new Department();
			dept.setName("Dept" + d);
			departmentRepo.save(dept);

			Employee deptManager = new Employee();
			deptManager.setFirstName("MngFirstname" + d);
			deptManager.setLastName("MngLastname" + d);
			dept.setDeptManager(deptManager);
			deptManager = employeeRepo.save(deptManager);
			
			dept.setDeptManager(deptManager);

			dept.getEmployees().add(deptManager);
			deptManager.setDepartment(dept); // redundant with dept.getEmployees().add(deptManager) ??

			for(int i = 2; i < 50; i++) {
				Employee emp = new Employee();
				emp.setFirstName("DeptEmp" + d + "Firstname" + i);
				emp.setLastName("DeptEmp" + d + "Lastname" + i);
				emp = employeeRepo.save(emp);

				emp.setManager(deptManager);
				
				dept.getEmployees().add(emp);
				emp.setDepartment(dept); // redundant with dept.getEmployees().add(emp) ??
			}
			
		}
		
	}
	
	
}
