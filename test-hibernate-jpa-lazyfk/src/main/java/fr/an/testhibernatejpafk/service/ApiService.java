package fr.an.testhibernatejpafk.service;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import fr.an.testhibernatejpafk.repository.DepartmentRepo;
import fr.an.testhibernatejpafk.repository.EmployeeRepo;
import lombok.val;

@Component
public class ApiService {

	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	private DepartmentRepo departmentRepo;

	public List<EmployeeDTO> listEmployeeDTOs() {
		val entities = employeeRepo.findAll();
		return DtoConverter.mapAsList(entities, EmployeeDTO.class);
	}

	public List<EmployeeDTO2> listEmployeeDTO2s() {
		val entities = employeeRepo.findAll();
		return DtoConverter.mapAsList(entities, EmployeeDTO2.class);
	}

	public List<EmployeeDTO3> listEmployeeDTO3s() {
		val entities = employeeRepo.findAll();
		return DtoConverter.mapAsList(entities, EmployeeDTO3.class);
	}

	public List<EmployeeDTO3Bis> listEmployeeDTO3Bis_usingJoins() {
		return employeeRepo.listEmployeeDTOs_usingJoin();
	}

	public List<EmployeeDTO4> listEmployeeDTO4s() {
		val entities = employeeRepo.findAll();
		return DtoConverter.mapAsList(entities, EmployeeDTO4.class);
	}
	
	
	public List<DepartmentDTO> listDepartmentDTOs() {
		val entities = departmentRepo.findAll();
		return DtoConverter.mapAsList(entities, DepartmentDTO.class);
	}

	public List<DepartmentDTO2> listDepartmentDTO2s() {
		val entities = departmentRepo.findAll();
		return DtoConverter.mapAsList(entities, DepartmentDTO2.class);
	}

	public List<DepartmentDTO3> listDepartmentDTO3s() {
		val entities = departmentRepo.findAll();
		return DtoConverter.mapAsList(entities, DepartmentDTO3.class);
	}

	public List<DepartmentDTO3Bis> listDepartmentDTO3Bis_usingJoins() {
		return departmentRepo.listDepartmentDTOs_usingJoin();
	}

	public List<DepartmentDTO4> listDepartmentDTO4s() {
		val entities = departmentRepo.findAll();
		return DtoConverter.mapAsList(entities, DepartmentDTO4.class);
	}

}
