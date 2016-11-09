package fr.an.tools.git2neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label="DirEntry")
public class DirEntryEntity {

	private String name;
	
	private String fileMode;
	
	@Relationship(type="tree")
	private RevTreeEntity tree;

	// ------------------------------------------------------------------------

	public DirEntryEntity() {
	}

	// ------------------------------------------------------------------------
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileMode() {
		return fileMode;
	}

	public void setFileMode(String fileMode) {
		this.fileMode = fileMode;
	}

	public RevTreeEntity getTree() {
		return tree;
	}

	public void setTree(RevTreeEntity tree) {
		this.tree = tree;
	}
	
	// ------------------------------------------------------------------------
	
	
}
