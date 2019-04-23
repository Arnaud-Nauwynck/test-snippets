package fr.an.testhibernatejpafk.dto;

import lombok.Data;

@Data
public class DepartmentDTO2 {

	private int id;
	
	private int version;

	private String name;
	
	// ignore.. private List<EmployeeDTO> employees = new ArrayList<>();
	
	private EmployeeIdDTO deptManager;
		
}
