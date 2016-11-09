package fr.an.tools.git2neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label="SymLink")
public class SymLinkEntity extends RevTreeEntity {

	private String linkPath;
	
	// ------------------------------------------------------------------------

	public SymLinkEntity() {
	}

	// ------------------------------------------------------------------------
	
	public String getLinkPath() {
		return linkPath;
	}

	public void setLinkPath(String p) {
		this.linkPath = p;
	}
	
	// ------------------------------------------------------------------------
		
	
}
