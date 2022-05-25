package fr.an.hadoop.fs.dirserver.service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import fr.an.hadoop.fs.dirserver.dto.FileStatusDTO;
import fr.an.hadoop.fs.dirserver.dto.MountedDirDTO;
import fr.an.hadoop.fs.dirserver.util.LsUtils;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DirService {

	private final Object lock = new Object();
	
	private final Configuration hadoopConf;
	
	// @GuardedBy("lock")
	private volatile ImmutableMap<String,MountedDir> mountedDirs = ImmutableMap.of();
	
	// ------------------------------------------------------------------------
	
	public DirService(Configuration hadoopConf) {
		this.hadoopConf = hadoopConf;
	}

	// ------------------------------------------------------------------------

	public List<MountedDirDTO> getMountedDirs() {
		return LsUtils.map(mountedDirs.values(), x->x.toDTO());
	}

	public MountedDirDTO addMountedDir(MountedDirDTO req) {
		synchronized (lock) {
			String name = req.name;
			MountedDir found = mountedDirs.get(name);
			if (found != null) {
				throw new IllegalArgumentException("mounted dir already found '" + name + "'");
			}
			String baseMountUrl = req.baseMountUrl;
			// TODO only Hadoop api supported yet
			URI baseURI = new URI(baseMountUrl);
			val fs = FileSystem.get(baseURI, hadoopConf);
			MountedSubTreeFileSystem subTreeFs = new HadoopMountedSubTreeFileSystem(baseMountUrl, fs);
			
			MountedDir res = new MountedDir(name, baseMountUrl, subTreeFs);
			this.mountedDirs = ImmutableMap.<String,MountedDir>builder()
					.putAll(mountedDirs).put(name, res)
					.build();
			// TODO notify?
			
			return res.toDTO();
		}
	}

	public MountedDirDTO removeMountedDir(String name) {
		synchronized (lock) {
			MountedDir found = mountedDirs.get(name);
			if (found == null) {
				throw new IllegalArgumentException("mounted dir not found '" + name + "'");
			}
			val tmp = new LinkedHashMap<>(mountedDirs);
			tmp.remove(name);
			// TODO cleanup? notify?
			this.mountedDirs = ImmutableMap.copyOf(tmp);
		
			return found.toDTO();
		}
	}
	
	@AllArgsConstructor
	protected static class ResolvedMountedPath {
		public final MountedDir mountedDir;
		public final String[] subpath;
	}
	
	protected ResolvedMountedPath resolve(String mountedPath) {
		String[] pathElts = mountedPath.split("/");
		val rootMountedDirs = this.mountedDirs;
		for(int i = 0; i < pathElts.length; i++) {
			String name = pathElts[i];
			val found = rootMountedDirs.get(name);
			// currently implemented: only 1 mount at root level
			break;
		}
	}
	
	public FileStatusDTO getFileStatus(String path) {
		String[] pathElts = path.split("/");
		
		FileStatus res;
		log.info("getFileStatus " + path);
		// TODO caching ..
		FileSystem fileSystem = fileSystemPathResolver.resolve(path);
		try {
			res = fileSystem.getFileStatus(path);
		} catch (IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
		return res;
	}

	public FileStatus[] listStatus(Path path) {
		FileStatus[] res;
		log.info("listStatus " + path);
		// TODO caching ..
		FileSystem fileSystem = fileSystemPathResolver.resolve(path);
		try {
			res = fileSystem.listStatus(path);
		} catch (IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
		return res;
	}

	public List<FileStatus> listStatus(List<Path> pathes) {
		List<FileStatus> res = new ArrayList<>();
		log.info("listStatus " + pathes);
		// TODO caching ..
		for(val path: pathes) {
			FileSystem fileSystem = fileSystemPathResolver.resolve(path);
			try {
				FileStatus[] tmpres = fileSystem.listStatus(path);
				res.addAll(Arrays.asList(tmpres));
			} catch (IOException ex) {
				throw new RuntimeException("Failed", ex);
			}
		}
		return res;
	}

	public FileStatus[] globStatus(Path pathPattern) {
		FileStatus[] res;
		log.info("globStatus " + pathPattern);
		// TODO caching ..
		FileSystem fileSystem = fileSystemPathResolver.resolve(pathPattern);
		try {
			res = fileSystem.globStatus(pathPattern);
		} catch (IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
		return res;
	}

	public void notifyCreate(Path path, 
			FsPermission permission, boolean overwrite, int bufferSize,
			short block_replication, long blockSize) {
		log.info("notifyCreate " + path);
		// nop
		// TODO update/evict caching..
	}

	public void notifyRename(Path srcPath, Path dstPath) {
		log.info("notifyCreate " + srcPath + " " + dstPath);
		// nop
		// TODO update/evict caching..
	}

	public void notifyDelete(Path path, boolean recursive) {
		log.info("notifyDelete " + path + ((recursive)? " recursive" : ""));
		// nop
		// TODO update/evict caching..
	}

}
