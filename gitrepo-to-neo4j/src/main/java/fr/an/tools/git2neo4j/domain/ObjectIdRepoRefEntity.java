package fr.an.tools.git2neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label="RepoRef")
public class ObjectIdRepoRefEntity extends AbstractRepoRefEntity {

	@Relationship(type = "refCommit")
	private RevCommitEntity refCommit;
	
	// ------------------------------------------------------------------------

	public ObjectIdRepoRefEntity() {
	}

	// ------------------------------------------------------------------------
	
	public RevCommitEntity getRefCommit() {
		return refCommit;
	}

	public void setRefCommit(RevCommitEntity p) {
		this.refCommit = p;
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "ObjectIdRepoRefEntity [" + id + " '" + name + "', refCommit:" + refCommit + "]";
	}

}
