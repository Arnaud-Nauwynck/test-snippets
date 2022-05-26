package fr.an.hadoop.fs.dirserver.fsdata.hadoop;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import fr.an.hadoop.fs.dirserver.fsdata.NodeFsData;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsData.DirNodeFsData;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsData.FileNodeFsData;
import fr.an.hadoop.fs.dirserver.fsdata.NodeFsDataProvider;
import fr.an.hadoop.fs.dirserver.util.LoggingCallStats;
import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * implementation of NodeFsDataProvider delegating to Hadoop FileSystem
 */
@Slf4j
public class HadoopNodeFsDataProvider extends NodeFsDataProvider {

	@Getter
	final String baseUrl;

	@Getter
	private final FileSystem fs;
	
	@Getter
	private final Path basePath;
	
	private final LoggingCallStats fsGetStatusStats;
	private final LoggingCallStats fsListStatusStats;
	
	// ------------------------------------------------------------------------
	
	public HadoopNodeFsDataProvider(String baseUrl, FileSystem fs) {
		this.baseUrl = baseUrl;
		this.fs = fs;
		this.basePath = new Path(baseUrl);
		this.fsGetStatusStats = new LoggingCallStats("HadoopFS " + baseUrl + " .getStatus()", "getStatus", 1000);
		this.fsListStatusStats = new LoggingCallStats("HadoopFS " + baseUrl + " .listStatus()", "listStatus", 1000);
	}

	// ------------------------------------------------------------------------

	@Override
	public NodeFsData queryNodeFsData(String[] subpath) {
		Path hadoopPath = subpathToHadoopPath(subpath);
		
		FileStatus hadoopFileStatus = fsGetFileStatus(hadoopPath);
		if (hadoopFileStatus == null) {
			return null;
		}
		String name = subpath[subpath.length-1];
		long creationTime = hadoopFileStatus.getModificationTime(); // info not known in hadoop.. use modificationTime?
		long lastModifiedTime = hadoopFileStatus.getModificationTime();
		ImmutableMap<String,Object> extraFsAttrs = ImmutableMap.of(
				// TOADD..
				);
		
		NodeFsData res;
		if (hadoopFileStatus.isDirectory()) {
			// also query child files
			FileStatus[] hadoopChildLs = fsListStatus(hadoopPath);
			val child = ImmutableSet.<String>builder();
			for(val hadoopChild: hadoopChildLs) {
				child.add(hadoopChild.getPath().getName());
			}
			res = new DirNodeFsData(name, creationTime, lastModifiedTime, extraFsAttrs, child.build());
		} else if (hadoopFileStatus.isFile()) {
			long fileLength = hadoopFileStatus.getLen();
			res = new FileNodeFsData(name, creationTime, lastModifiedTime, extraFsAttrs, fileLength);
		} else if (hadoopFileStatus.isSymlink()) {
			log.info("ignore Hadoop symlink");
			res = null;
		} else {
			log.error("should not occur");
			res = null;
		}
		return res;
	}

	// ------------------------------------------------------------------------
	
	protected Path subpathToHadoopPath(String[] subpath) {
		return new Path(basePath, String.join("/", subpath));
	}
	
	protected FileStatus fsGetFileStatus(Path hadoopPath) {
		FileStatus res;
		long startTime = System.currentTimeMillis();
		try {
			res = fs.getFileStatus(hadoopPath);
		} catch(FileNotFoundException ex) {
			long millis = System.currentTimeMillis() - startTime;
			fsGetStatusStats.incrLog(millis);
			return null;
		} catch (IOException ex) {
			long millis = System.currentTimeMillis() - startTime;
			fsGetStatusStats.incrLogFailed(millis, ex);
			throw new RuntimeException("Failed", ex);
		}
		long millis = System.currentTimeMillis() - startTime;
		fsGetStatusStats.incrLog(millis);
		
		return res;
	}

	protected FileStatus[] fsListStatus(Path hadoopPath) {
		FileStatus[] res;
		long startTime = System.currentTimeMillis();
		try {
			res = fs.listStatus(hadoopPath);
		} catch(FileNotFoundException ex) {
			// should not occur (query just before)
			long millis = System.currentTimeMillis() - startTime;
			fsListStatusStats.incrLogFailed(millis, ex);
			return null;
		} catch (IOException ex) {
			long millis = System.currentTimeMillis() - startTime;
			fsListStatusStats.incrLogFailed(millis, ex);
			throw new RuntimeException("Failed", ex);
		}
		long millis = System.currentTimeMillis() - startTime;
		fsListStatusStats.incrLog(millis);
		return res;
	}
	
}
