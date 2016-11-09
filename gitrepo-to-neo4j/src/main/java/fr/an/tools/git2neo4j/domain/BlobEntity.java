package fr.an.tools.git2neo4j.domain;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label="Blob")
public class BlobEntity extends RevTreeEntity {

	private int blobLength;
	
	// ------------------------------------------------------------------------

	public BlobEntity() {
	}

	// ------------------------------------------------------------------------
	
	public int getBlobLength() {
		return blobLength;
	}

	public void setBlobLength(int blobLength) {
		this.blobLength = blobLength;
	}

	// ------------------------------------------------------------------------
		
	
}
