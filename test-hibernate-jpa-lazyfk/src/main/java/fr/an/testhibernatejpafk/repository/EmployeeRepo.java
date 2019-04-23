package fr.an.testhibernatejpafk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.an.testhibernatejpafk.domain.Employee;
import fr.an.testhibernatejpafk.dto.EmployeeDTO3Bis;

public interface EmployeeRepo extends JpaRepository<Employee,Integer> {

	@Query("SELECT new fr.an.testhibernatejpafk.dto.EmployeeDTO3Bis("
			+ "x.id, x.version, x.firstName, x.lastName, x.email, x.address,"
			+ "d.id, d.name,"
			+ "m.id, m.firstName, m.lastName, m.email"
			+ ") FROM Employee x" 
			+ "  LEFT JOIN x.department d" 
			+ "  LEFT JOIN x.manager m")
	List<EmployeeDTO3Bis> listEmployeeDTOs_usingJoin();
	
}
