package fr.an.hadoop.fs.dirserver.service;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Service;

@Service
public class CachingFileSystemPathResolver {
	
	private FileSystem fileSystem;

	
	public CachingFileSystemPathResolver(FileSystem fileSystem) {
		this.fileSystem = fileSystem;
	}

	public FileSystem resolve(Path path) {
		// TODO Auto-generated method stub
		return fileSystem;
	}

}
