package fr.an.tools.git2neo4j.domain;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type="has_entry")
public class DirEntryEntity {

	private Long id;
	
	@StartNode 
	private DirTreeEntity parentDir;
	
	@EndNode   
	private RevTreeEntity child;
	
	private String name;
	
	private int fileMode;
	
	// ------------------------------------------------------------------------

	public DirEntryEntity() {
	}
	
	public DirEntryEntity(DirTreeEntity parentDir, RevTreeEntity child, String name, int fileMode) {
		this();
		this.parentDir = parentDir;
		this.child = child;
		this.name = name;
		this.fileMode = fileMode;
	}


	// ------------------------------------------------------------------------

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public DirTreeEntity getParentDir() {
		return parentDir;
	}
	
	public void setParentDir(DirTreeEntity parentDir) {
		this.parentDir = parentDir;
	}
	
	public RevTreeEntity getChild() {
		return child;
	}

	public void setChild(RevTreeEntity child) {
		this.child = child;
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
	
	
	// ------------------------------------------------------------------------
	
	
}
