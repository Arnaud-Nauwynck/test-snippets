package fr.an.testhibernatejpafk.dto;

import lombok.Data;

@Data
public class DepartmentDTO {

	private int id;
	
	private int version;

	private String name;
	
	// ignore.. private List<EmployeeDTO> employees = new ArrayList<>();
	
	private Integer deptManagerId; // need custom mapper for "deptManager.Id -> deptManagerId", or redundant entity fields "deptManagerId"
		
}
