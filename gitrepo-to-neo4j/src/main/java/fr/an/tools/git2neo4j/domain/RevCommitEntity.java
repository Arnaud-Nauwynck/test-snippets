package fr.an.tools.git2neo4j.domain;

import java.util.List;

import org.eclipse.jgit.lib.ObjectId;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class RevCommitEntity {

	@GraphId 
	private Long id; // internal generated id, not the SHA1 !!
	
	/** SHA-1 .. cf ObjectId */
	private String sha1;
	
	@Relationship(type = "RATED")
	private List<RevCommitEntity> parents;
	
	private String shortMessage;
	private String fullMessage;
	
	// ------------------------------------------------------------------------

	public RevCommitEntity() {
	}

	// ------------------------------------------------------------------------
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String commitId) {
		this.sha1 = commitId;
	}
	
	public ObjectId getCommitId() {
		return sha1 != null? ObjectId.fromString(sha1) : null;
	}
	
	public void setCommitId(ObjectId commitId) {
		this.sha1 = commitId != null? commitId.name() : null;
	}
	
	
	public List<RevCommitEntity> getParents() {
		return parents;
	}

	public void setParents(List<RevCommitEntity> parents) {
		this.parents = parents;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public void setShortMessage(String p) {
		this.shortMessage = p;
	}

	public String getFullMessage() {
		return fullMessage;
	}

	public void setFullMessage(String fullMessage) {
		this.fullMessage = fullMessage;
	}

	@Override
	public String toString() {
		return "RevCommitEntity [id=" + id + ", " + sha1 + "]";
	}
	
}
