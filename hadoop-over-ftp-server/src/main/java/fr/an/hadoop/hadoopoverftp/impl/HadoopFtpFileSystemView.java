package fr.an.hadoop.hadoopoverftp.impl;

import java.io.IOException;

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
public class HadoopFtpFileSystemView implements FileSystemView {

	private final org.apache.hadoop.fs.FileSystem delegateFileSystem;
	private final String delegateBaseUri;

	private HadoopFtpFile baseDir;
	private HadoopFtpFile workingDir;
	
	// ------------------------------------------------------------------------
	
	public HadoopFtpFileSystemView(org.apache.hadoop.fs.FileSystem delegateFileSystem, String delegateBaseUri) {
		super();
		this.delegateFileSystem = delegateFileSystem;
		this.delegateBaseUri = delegateBaseUri;
		
		baseDir = resolveFile("/");
		workingDir = baseDir;
	}

	// ------------------------------------------------------------------------

	@Override
	public FtpFile getHomeDirectory() throws FtpException {
		return baseDir;
	}

	@Override
	public FtpFile getWorkingDirectory() throws FtpException {
		return workingDir;
	}

	@Override
	public boolean changeWorkingDirectory(String dir) throws FtpException {
		log.info("changeWorkingDirectory '" + dir + "'");
		this.workingDir = resolveFile(dir);
		return true;
	}

	@Override
	public FtpFile getFile(String file) throws FtpException {
		return resolveFile(file);
	}

	private HadoopFtpFile resolveFile(String file) {
		log.info("resolveFile '" + file + "'");
		org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(delegateBaseUri + file);
		org.apache.hadoop.fs.FileStatus fileStatus;
		try {
			fileStatus = delegateFileSystem.getFileStatus(path);
		} catch (IOException e) {
			fileStatus = null;
		}
		return new HadoopFtpFile(delegateFileSystem, path, fileStatus, file);
	}

	@Override
	public boolean isRandomAccessible() throws FtpException {
		return true;
	}

	@Override
	public void dispose() {
	}

}
