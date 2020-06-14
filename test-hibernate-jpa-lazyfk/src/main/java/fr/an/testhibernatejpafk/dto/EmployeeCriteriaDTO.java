package fr.an.testhibernatejpafk.dto;

import lombok.Data;

@Data
public class EmployeeCriteriaDTO {

	// => where employee.firstName like '%<<firstNameContains>>%'
	private String firstNameContains;

	// => where employee.firstName = firstNameContains
	private String firstNameEq;

	private String lastName;
	
	private String email;
	
	private String address;

	
}
