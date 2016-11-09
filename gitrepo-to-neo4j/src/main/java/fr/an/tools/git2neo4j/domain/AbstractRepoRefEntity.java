package fr.an.tools.git2neo4j.domain;

import org.neo4j.ogm.annotation.GraphId;

public abstract class AbstractRepoRefEntity {

	@GraphId 
	private Long id;

	private String name;
		
	// ------------------------------------------------------------------------

	public AbstractRepoRefEntity() {
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

	public void setName(String name) {
		this.name = name;
	}

	// ------------------------------------------------------------------------


}
