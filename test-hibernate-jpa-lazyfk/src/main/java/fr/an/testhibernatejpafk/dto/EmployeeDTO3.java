package fr.an.testhibernatejpafk.dto;

import lombok.Data;

@Data
public class EmployeeDTO3 {

	private int id;
	
	private int version;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String address;

	private DepartmentNameIdDTO department;

	private EmployeeNameIdDTO manager;
	
}
