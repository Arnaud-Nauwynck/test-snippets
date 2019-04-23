package fr.an.testhibernatejpafk.dto;

import lombok.Data;

@Data
public class EmployeeDTO4 {

	private int id;
	
	private int version;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String address;

	private DepartmentDTO department; // possible infinite loop..

	private EmployeeDTO manager; // possible infinite loop
	
}
