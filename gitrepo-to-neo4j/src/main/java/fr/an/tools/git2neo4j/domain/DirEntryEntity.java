package fr.an.tools.git2neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label="DirEntry")
public class DirEntryEntity {

	private Long id;
	
	private String name;
	
	private int fileMode;
	
	@Relationship(type="tree")
	private RevTreeEntity tree;

	// ------------------------------------------------------------------------

	public DirEntryEntity() {
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

	public int getFileMode() {
		return fileMode;
	}

	public void setFileMode(int p) {
		this.fileMode = p;
	}

	public RevTreeEntity getTree() {
		return tree;
	}

	public void setTree(RevTreeEntity tree) {
		this.tree = tree;
	}
	
	// ------------------------------------------------------------------------
	
	
}
