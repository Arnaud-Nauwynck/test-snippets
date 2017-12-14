package com.example.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class UserProject {

	@Id
	private int id;
	
	@ManyToOne
	private Employee employee;
	
	// ------------------------------------------------------------------------

	public UserProject() {
	}

	// ------------------------------------------------------------------------
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}


	
}
