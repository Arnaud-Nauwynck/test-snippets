package fr.an.testhibernatejpafk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.an.testhibernatejpafk.domain.Department;
import fr.an.testhibernatejpafk.dto.DepartmentDTO3Bis;

public interface DepartmentRepo extends JpaRepository<Department,Integer> {

	@Query("SELECT new fr.an.testhibernatejpafk.dto.DepartmentDTO3Bis("
			 + "x.id, x.version, x.name, "
			 + "d.id, d.firstName, d.lastName, d.email"
			 + ") FROM Department x" 
			 + "  LEFT JOIN x.deptManager d")
	List<DepartmentDTO3Bis> listDepartmentDTOs_usingJoin();
	
}
