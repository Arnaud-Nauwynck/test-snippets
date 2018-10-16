package fr.an.tests.mockspringdata.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class BEntity {

	@Id
	@GeneratedValue
	@Getter
	private int id;
	
	@Getter @Setter
	private String name;

	public BEntity() {
	}
	
	public BEntity(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	
}
