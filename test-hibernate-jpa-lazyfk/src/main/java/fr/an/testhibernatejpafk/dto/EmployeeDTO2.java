package fr.an.testhibernatejpafk.dto;

import lombok.Data;

@Data
public class EmployeeDTO2 {

	private int id;
	
	private int version;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String address;

	private DepartmentIdDTO department;

	private EmployeeIdDTO manager;
	
}
