package fr.an.tools.git2neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label="RepoRef")
public class RepoRefEntity extends AbstractRepoRefEntity {

	@Relationship(type = "refCommit")
	private RevCommitEntity refCommit;
	
	// ------------------------------------------------------------------------

	public RepoRefEntity() {
	}

	// ------------------------------------------------------------------------
	
	public RevCommitEntity getRefCommit() {
		return refCommit;
	}

	public void setRefCommit(RevCommitEntity p) {
		this.refCommit = p;
	}

	// ------------------------------------------------------------------------


}
