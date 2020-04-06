package fr.an.testspringbootfastcl.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Department {

	@Id
	private int id;
	
	private String name;
	
	// ------------------------------------------------------------------------

	public Department() {
	}

	// ------------------------------------------------------------------------
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
