package fr.an.tools.git2neo4j.domain;

import org.eclipse.jgit.lib.ObjectId;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Labels;

/**
 * abstract class ... sub-classes: DirTreeEntity / BlobEntity
 * types: DirTree | Blob | SymLink | GitLink
 */
// @NodeEntity(label="RevTree")
@Labels(defaultValue="Tree")
public abstract class RevTreeEntity {

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

	public ObjectId getObjectId() {
		return sha1 != null? ObjectId.fromString(sha1) : null;
	}
	
	public void setObjectId(ObjectId objectId) {
		this.sha1 = objectId != null? objectId.name() : null;
	}
	
}
