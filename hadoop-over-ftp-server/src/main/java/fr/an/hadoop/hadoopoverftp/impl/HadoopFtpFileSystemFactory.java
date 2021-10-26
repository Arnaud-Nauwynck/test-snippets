package fr.an.hadoop.hadoopoverftp.impl;

import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.User;
import org.apache.hadoop.fs.FileSystem;

/**
 * implements apache ftp FileSystemFactory
 */
public class HadoopFtpFileSystemFactory implements FileSystemFactory {

	private final org.apache.hadoop.fs.FileSystem delegateFileSystem;
	private final String delegateBaseUri;
	
	
	public HadoopFtpFileSystemFactory(FileSystem delegateFileSystem, String delegateBaseUri) {
		this.delegateFileSystem = delegateFileSystem;
		this.delegateBaseUri = delegateBaseUri;
	}

	@Override
	public FileSystemView createFileSystemView(User user) {
		return new HadoopFtpFileSystemView(delegateFileSystem, delegateBaseUri);
	}
	
}
