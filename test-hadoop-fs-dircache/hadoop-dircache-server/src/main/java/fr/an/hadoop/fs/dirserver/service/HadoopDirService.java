package fr.an.hadoop.fs.dirserver.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HadoopMountedSubTreeFileSystem extends MountedSubTreeFileSystem {

	private final FileSystem fs;
	private final Path basePath;
	
	// ------------------------------------------------------------------------
	
	public HadoopMountedSubTreeFileSystem(String baseUrl, FileSystem fs) {
		super(baseUrl);
		this.fs = fs;
		this.basePath = new Path(baseUrl);
	}

	// ------------------------------------------------------------------------
	
	protected Path toPath(String[] subpath) {
		return new Path(basePath, String.join("/", subpath));
	}
	
	public FileStatus getFileStatus(String[] subpath) {
		Path path = toPath(subpath);
		FileStatus res;
		log.info("getFileStatus " + path);
		try {
			res = fs.getFileStatus(path);
		} catch (IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
		return res;
	}

	public FileStatus[] listStatus(String[] subpath) {
		FileStatus[] res;
		Path path = toPath(subpath);
		log.info("listStatus " + path);
		try {
			res = fs.listStatus(path);
		} catch (IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
		return res;
	}

//	public List<FileStatus> listStatus(List<Path> pathes) {
//		List<FileStatus> res = new ArrayList<>();
//		log.info("listStatus " + pathes);
//		// TODO caching ..
//		for(val path: pathes) {
//			FileSystem fileSystem = fileSystemPathResolver.resolve(path);
//			try {
//				FileStatus[] tmpres = fileSystem.listStatus(path);
//				res.addAll(Arrays.asList(tmpres));
//			} catch (IOException ex) {
//				throw new RuntimeException("Failed", ex);
//			}
//		}
//		return res;
//	}
//
//	public FileStatus[] globStatus(Path pathPattern) {
//		FileStatus[] res;
//		log.info("globStatus " + pathPattern);
//		// TODO caching ..
//		FileSystem fileSystem = fileSystemPathResolver.resolve(pathPattern);
//		try {
//			res = fileSystem.globStatus(pathPattern);
//		} catch (IOException ex) {
//			throw new RuntimeException("Failed", ex);
//		}
//		return res;
//	}
//
//	public void notifyCreate(Path path, 
//			FsPermission permission, boolean overwrite, int bufferSize,
//			short block_replication, long blockSize) {
//		log.info("notifyCreate " + path);
//		// nop
//		// TODO update/evict caching..
//	}
//
//	public void notifyRename(Path srcPath, Path dstPath) {
//		log.info("notifyCreate " + srcPath + " " + dstPath);
//		// nop
//		// TODO update/evict caching..
//	}
//
//	public void notifyDelete(Path path, boolean recursive) {
//		log.info("notifyDelete " + path + ((recursive)? " recursive" : ""));
//		// nop
//		// TODO update/evict caching..
//	}
//
//	@Override
//	public FileStatus gitFileStatus(String[] path) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
