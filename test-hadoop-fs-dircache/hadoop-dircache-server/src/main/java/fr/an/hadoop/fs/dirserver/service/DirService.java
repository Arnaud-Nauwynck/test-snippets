package fr.an.hadoop.fs.dirserver.service;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.an.hadoop.fs.dirserver.dto.MountedDirDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DirService {

	public List<MountedDirDTO> getMountedDirs() {
		return null; // TODO
	}

	public MountedDirDTO addMountedDir(MountedDirDTO req) {
		return null; // TODO
	}

	public MountedDirDTO removeMountedDir(String name) {
		return null; // TODO
	}
	
	
//	public FileStatusDTO getFileStatus(String path) {
//		String[] pathElts = path.split("/");
//		
//		FileStatus res;
//		log.info("getFileStatus " + path);
//		// TODO caching ..
//		FileSystem fileSystem = fileSystemPathResolver.resolve(path);
//		try {
//			res = fileSystem.getFileStatus(path);
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

}
