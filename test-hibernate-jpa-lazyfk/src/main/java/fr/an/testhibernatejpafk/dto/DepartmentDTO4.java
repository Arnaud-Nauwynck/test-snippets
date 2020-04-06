package fr.an.testhibernatejpafk.dto;

import lombok.Data;

@Data
public class DepartmentDTO4 {

	private int id;
	
	private int version;

	private String name;
	
	// ignore.. private List<EmployeeDTO> employees = new ArrayList<>();
	
	private EmployeeDTO deptManager;  // possible infinite loop...
		
}
