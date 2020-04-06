package fr.an.testhibernatejpafk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class EmployeeNameIdDTO {

	private int id;
	
	private String firstName;
	private String lastName;
	private String email;

}
