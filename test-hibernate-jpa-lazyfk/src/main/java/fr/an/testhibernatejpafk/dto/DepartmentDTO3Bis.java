package fr.an.testhibernatejpafk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DepartmentDTO3Bis {

	private int id;
	
	private int version;

	private String name;
	
	// ignore.. private List<EmployeeDTO> employees = new ArrayList<>();
	
	private EmployeeNameIdDTO deptManager;

	public DepartmentDTO3Bis(int id, int version, String name, 
			// inline EmployeeNameIdDTO deptManager ..
			Integer deptManager_id, String deptManager_firstName, String deptManager_lastName, String deptManager_email
			) {
		this(id, version, name,
				(deptManager_id != null)? new EmployeeNameIdDTO(deptManager_id, deptManager_firstName, deptManager_lastName, deptManager_email) : null
				);
	}
	
}
