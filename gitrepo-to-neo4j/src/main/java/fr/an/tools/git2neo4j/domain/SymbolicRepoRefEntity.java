package fr.an.tools.git2neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label="SymbolicRepoRef")
public class SymbolicRepoRefEntity extends AbstractRepoRefEntity {

	@Relationship(type = "ref")
	private AbstractRepoRefEntity ref;
	
	// ------------------------------------------------------------------------

	public SymbolicRepoRefEntity() {
	}

	// ------------------------------------------------------------------------
	
	public AbstractRepoRefEntity getRef() {
		return ref;
	}

	public void setRef(AbstractRepoRefEntity p) {
		this.ref = p;
	}

	// ------------------------------------------------------------------------


}
