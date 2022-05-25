package fr.an.hadoop.fs.dirserver.service;

import org.apache.hadoop.fs.FileStatus;

public abstract class MountedSubTreeFileSystem {

	public final String baseUrl;

	// ------------------------------------------------------------------------
	
	public MountedSubTreeFileSystem(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	// ------------------------------------------------------------------------

	public abstract FileStatus gitFileStatus(String[] path);
	
}
