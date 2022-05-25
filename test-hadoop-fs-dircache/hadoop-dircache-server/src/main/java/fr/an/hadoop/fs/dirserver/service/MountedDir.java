package fr.an.hadoop.fs.dirserver.service;

import fr.an.hadoop.fs.dirserver.dto.MountedDirDTO;

public class MountedDir {

	public final String name;
	
	public final String baseMountUrl;

	public final MountedSubTreeFileSystem subTreeFs;
	
	// ------------------------------------------------------------------------
	
	public MountedDir(String name, String baseMountUrl, MountedSubTreeFileSystem subTreeFs) {
		this.name = name;
		this.baseMountUrl = baseMountUrl;
		this.subTreeFs = subTreeFs;
	}

	// ------------------------------------------------------------------------

	public MountedDirDTO toDTO() {
		return new MountedDirDTO(name, baseMountUrl);
	}
}
