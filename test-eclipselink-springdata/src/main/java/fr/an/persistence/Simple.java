package fr.an.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Simple {

	@Id
	private int id;

	private String name;
	
	public Simple() {
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
