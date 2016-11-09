package fr.an.tools.git2neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label="SymbolicRepoRef")
public class SymbolicRepoRefEntity extends AbstractRepoRefEntity {

	@Relationship(type = "target")
	private AbstractRepoRefEntity target;
	
	// ------------------------------------------------------------------------

	public SymbolicRepoRefEntity() {
	}

	// ------------------------------------------------------------------------
	
	public AbstractRepoRefEntity getTarget() {
		return target;
	}

	public void setTarget(AbstractRepoRefEntity p) {
		this.target = p;
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "SymbolicRepoRef [" + id + " '" + name + "', target:" + target + "]";
	}

}
