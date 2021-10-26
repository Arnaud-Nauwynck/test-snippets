package fr.an.hadoop.hadoopoverftp.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HadoopFtpFile implements FtpFile {

	private final org.apache.hadoop.fs.FileSystem fileSystem;
	private final org.apache.hadoop.fs.Path path;
	private org.apache.hadoop.fs.FileStatus hadoopFileStatus;
	
	private String displayAbsolutePath; 
	
	// ------------------------------------------------------------------------
	
	public HadoopFtpFile(
			org.apache.hadoop.fs.FileSystem fileSystem,
			Path hadoopPath, 
			org.apache.hadoop.fs.FileStatus hadoopFileStatus,
			String displayAbsolutePath) {
		super();
		this.fileSystem = fileSystem;
		this.path = hadoopPath;
		this.hadoopFileStatus = hadoopFileStatus;
	}

	// ------------------------------------------------------------------------

	@Override
	public String getAbsolutePath() {
		return displayAbsolutePath;
	}

	@Override
	public String getName() {
		return path.getName();
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public boolean isDirectory() {
		return hadoopFileStatus.isDir();
	}

	@Override
	public boolean isFile() {
		return ! hadoopFileStatus.isDir();
	}

	@Override
	public boolean doesExist() {
		return null != hadoopFileStatus;
	}

	@Override
	public boolean isReadable() {
		// TODO hadoopFileStatus.getPermission().;
		return true;
	}

	@Override
	public boolean isWritable() {
		// TODO hadoopFileStatus.getPermission().;
		return true; 
	}

	@Override
	public boolean isRemovable() {
		return true;
	}

	@Override
	public String getOwnerName() {
		return hadoopFileStatus.getOwner();
	}

	@Override
	public String getGroupName() {
		return hadoopFileStatus.getGroup();
	}

	@Override
	public int getLinkCount() {
		return 0;
	}

	@Override
	public long getLastModified() {
		return hadoopFileStatus.getModificationTime();
	}

	@Override
	public boolean setLastModified(long time) {
		log.info("setLastModified not supported/implemented");
		return false;
	}

	@Override
	public long getSize() {
		return hadoopFileStatus.getLen();
	}

	@Override
	public Object getPhysicalFile() {
		return null;
	}

	@Override
	public boolean mkdir() {
		log.error("not implemented yet");
		return false;
	}

	@Override
	public boolean delete() {
		log.error("not implemented yet");
		return false;
	}

	@Override
	public boolean move(FtpFile destination) {
		log.error("not implemented yet");
		return false;
	}

	@Override
	public List<? extends FtpFile> listFiles() {
		List<HadoopFtpFile> res = new ArrayList<>();
		FileStatus[] childList;
		try {
			childList = fileSystem.listStatus(path);
		} catch (IOException ex) {
			log.error("Failed to list files '" + path + "'" + ex.getMessage());
			return res;
		}
		for(FileStatus child: childList) {
			String childDisplayPath = displayAbsolutePath + "/" + child.getPath().getName();
			HadoopFtpFile childFtp = new HadoopFtpFile(fileSystem, child.getPath(),
					child, childDisplayPath);
			res.add(childFtp);
		}
		return res;
	}

	@Override
	public OutputStream createOutputStream(long offset) throws IOException {
		log.error("not implemented yet");
		return null;
	}

	@Override
	public InputStream createInputStream(long offset) throws IOException {
		log.error("not implemented yet");
		return null;
	}

	
}
