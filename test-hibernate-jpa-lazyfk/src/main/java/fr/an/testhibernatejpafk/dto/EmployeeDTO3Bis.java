package fr.an.testhibernatejpafk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeDTO3Bis {

	private int id;
	
	private int version;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String address;

	private DepartmentNameIdDTO department;

	private EmployeeNameIdDTO manager;

	public EmployeeDTO3Bis(
			int id, int version, String firstName, String lastName, String email, String address,
			// inline .. DepartmentNameIdDTO department, 
			Integer department_id, String department_name,
			// inline .. EmployeeNameIdDTO manager
			Integer manager_id, String manager_firstName, String manager_lastName, String manager_email
			) {
		this(id, version, firstName, lastName, email, address,
				(department_id != null)? new DepartmentNameIdDTO(department_id, department_name) : null,
				(manager_id != null)? new EmployeeNameIdDTO(manager_id, manager_firstName, manager_lastName, manager_email) : null
				);
	}
	
	
}
