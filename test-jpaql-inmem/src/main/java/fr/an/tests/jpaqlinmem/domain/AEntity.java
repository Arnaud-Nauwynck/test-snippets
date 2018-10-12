package fr.an.tests.jpaqlinmem.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
public class AEntity {

	@Id
	@GeneratedValue
	@Getter
	private int id;
	
	@Getter @Setter
	private String name;

	@ManyToOne
	@Getter @Setter
	private BEntity b;

	public AEntity() {
	}
	
	public AEntity(int id, String name, BEntity b) {
		this.id = id;
		this.name = name;
		this.b = b;
	}

	
}
