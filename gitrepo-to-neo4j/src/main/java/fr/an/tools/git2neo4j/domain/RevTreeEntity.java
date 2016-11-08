package fr.an.tools.git2neo4j.domain;

import org.eclipse.jgit.lib.ObjectId;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class RevTreeEntity {

	@GraphId 
	private Long id; // internal generated id, not the SHA1 !!
	
	/** SHA-1 .. cf ObjectId */
	private String sha1;

	// ------------------------------------------------------------------------

	public RevTreeEntity() {
	}

	// ------------------------------------------------------------------------
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ObjectId getCommitId() {
		return sha1 != null? ObjectId.fromString(sha1) : null;
	}
	
	public void setCommitId(ObjectId commitId) {
		this.sha1 = commitId != null? commitId.name() : null;
	}
	
	
}
