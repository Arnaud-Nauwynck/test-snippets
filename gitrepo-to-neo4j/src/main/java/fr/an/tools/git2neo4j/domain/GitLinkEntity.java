package fr.an.tools.git2neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label="GitLink")
public class GitLinkEntity extends RevTreeEntity {

	private String url;
	
	// ------------------------------------------------------------------------

	public GitLinkEntity() {
	}

	// ------------------------------------------------------------------------
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	// ------------------------------------------------------------------------
		
	
}
