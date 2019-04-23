package fr.an.testhibernatejpafk.dto;

import lombok.Data;

@Data
public class EmployeeDTO {

	private int id;
	
	private int version;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String address;

	private Integer departmentId;

	private Integer managerId;
	
}
