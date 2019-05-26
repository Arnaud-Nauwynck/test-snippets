package fr.an.tools.git2neo4j.domain;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label="Repo")
public class RepoEntity {

	@Id 
	private Long id;

	private String name;
	
	private String url;
	
	@Relationship(type="refs")
	private List<AbstractRepoRefEntity> refs = new ArrayList<>();
	
	// ------------------------------------------------------------------------

	public RepoEntity() {
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<AbstractRepoRefEntity> getRefs() {
		return refs;
	}

	public void setRefs(List<AbstractRepoRefEntity> refs) {
		this.refs = refs;
	}
	
	// ------------------------------------------------------------------------
	

}
