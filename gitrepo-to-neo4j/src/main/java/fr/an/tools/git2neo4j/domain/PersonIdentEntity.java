package fr.an.tools.git2neo4j.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label="Person")
public class PersonIdentEntity {

	@GraphId 
	private Long id; // internal generated id, not name/email
	
	private String name;
	private String emailAddress;
	
	// ------------------------------------------------------------------------

	public PersonIdentEntity() {
	}

	// ------------------------------------------------------------------------
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String p) {
		this.name = p;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String p) {
		this.emailAddress = p;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "Person[" + id + " " + name + "]";
	}
	
}
