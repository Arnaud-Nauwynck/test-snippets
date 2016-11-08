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
	
	@Relationship(type = "parent")
	private List<RevCommitEntity> parents;
	
	private String shortMessage;
	private String fullMessage;
	
	private int commitTime;

	@Relationship(type = "author")
	private PersonIdentEntity authorIdent;
	
	@Relationship(type = "committer")
	private PersonIdentEntity committerIdent;

	@Relationship(type = "revTree")
	private RevTreeEntity revTree;

	// @Relationship(type = "footerLines")
	// List<FooterLine> footerLines;
	
	// ------------------------------------------------------------------------

	public RevCommitEntity() {
	}

	// ------------------------------------------------------------------------
	
	public Long getId() {
		return id;
	}

	public void setId(Long p) {
		this.id = p;
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
	
	public void setCommitId(ObjectId p) {
		this.sha1 = p != null? p.name() : null;
	}
	
	
	public List<RevCommitEntity> getParents() {
		return parents;
	}

	public void setParents(List<RevCommitEntity> p) {
		this.parents = p;
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

	public void setFullMessage(String p) {
		this.fullMessage = p;
	}
	
	public int getCommitTime() {
		return commitTime;
	}

	public void setCommitTime(int p) {
		this.commitTime = p;
	}

	public PersonIdentEntity getAuthorIdent() {
		return authorIdent;
	}

	public void setAuthorIdent(PersonIdentEntity p) {
		this.authorIdent = p;
	}

	public PersonIdentEntity getCommitterIdent() {
		return committerIdent;
	}

	public void setCommitterIdent(PersonIdentEntity p) {
		this.committerIdent = p;
	}
	
	public RevTreeEntity getRevTree() {
		return revTree;
	}
	
	public void setRevTree(RevTreeEntity revTree) {
		this.revTree = revTree;
	}
	
	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "RevCommitEntity [id=" + id + ", " + sha1 + "]";
	}
	
}
