package fr.an.testhibernatejpafk.dto;

import lombok.Data;

@Data
public class DepartmentDTO3 {

	private int id;
	
	private int version;

	private String name;
	
	// ignore.. private List<EmployeeDTO> employees = new ArrayList<>();
	
	private EmployeeNameIdDTO deptManager;
		
}
