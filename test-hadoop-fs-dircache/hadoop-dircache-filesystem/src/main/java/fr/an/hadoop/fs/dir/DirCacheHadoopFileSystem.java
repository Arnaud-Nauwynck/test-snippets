package fr.an.hadoop.fs.dir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;

import fr.an.hadoop.fs.dir.cli.DirCacheFsHttpClient;
import lombok.val;

/**
 * 
 *
 */
public class DirCacheHadoopFileSystem extends AbstractWrappedFileSystem {

	private Configuration conf;
	
	private String baseDirServerUrl;
	private DirCacheFsHttpClient dirCacheClient;
	
	// --------------------------------------------------------------------------------------------

	/** constructor called by introspection, from Hadoop */
	public DirCacheHadoopFileSystem() {
	}

	// override lyfecycle methods of org.apache.hadoop.fs.FileSystem
	// --------------------------------------------------------------------------------------------

	@Override
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	@Override
	public Configuration getConf() {
		return conf;
	}

	@Override
	public void initialize(URI name, Configuration conf) throws IOException {
		super.initialize(name, conf);
		String scheme = name.getScheme();
		String fsName = scheme;
		String underlingFsURIText = conf.get("fs." + fsName + ".underlingFsURI");
		URI underlyingFsURI;
		try {
			underlyingFsURI = new URI(underlingFsURIText);
		} catch (URISyntaxException ex) {
			throw new RuntimeException("Failed", ex);
		}
		this.delegate = FileSystem.get(underlyingFsURI, conf);
		// already done?
		// delegate.setConf(conf);
		// delegate.initialize(name, conf);

		this.baseDirServerUrl = conf.get("fs." + fsName + ".baseDirServerUrl");
		this.dirCacheClient = new DirCacheFsHttpClient(baseDirServerUrl);
		
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}


	// override for delegating metadata dirs to dirServer ... otherwise use delegate to underlying FileSystem
	// when exception, fallback to delegate to underlying FileSystem
	// ------------------------------------------------------------------------
	

	@Override
	public FileStatus getFileStatus(Path f) throws IOException {
		FileStatus res;
		try {
			res = dirCacheClient.getFileStatus(f);
		} catch(Exception ex) {
			res = delegate.getFileStatus(f);
		}
		return res;
	}
	
	
//	@Override
//	public boolean exists(Path f) throws IOException {
//		// => getFileStatus()
//		return super.exists(f);
//	}
//
//	@Override
//	public boolean isDirectory(Path f) throws IOException {
//		// => getFileStatus()
//		return super.isDirectory(f);
//	}
//
//	@Override
//	public boolean isFile(Path f) throws IOException {
//		// => getFileStatus()
//		return super.isFile(f);
//	}

	@Override
	public FileStatus[] listStatus(Path f) throws FileNotFoundException, IOException {
		FileStatus[] res = doListStatus(f);
		return res;
	}

	protected FileStatus[] doListStatus(Path f) throws FileNotFoundException, IOException {
		FileStatus[] res;
		try {
			res = dirCacheClient.listStatus(f);
		} catch(Exception ex) {
			res = delegate.listStatus(f);
		}
		return res;
	}
	
	@Override
	public FileStatus[] listStatus(Path f, PathFilter filter) throws FileNotFoundException, IOException {
		// may test for downcast to standard sub-classes? GlobFilter ... but no public pattern field/getter  
		FileStatus[] unfilteredRes = doListStatus(f);
		return filter(unfilteredRes, filter);
	}

	private static FileStatus[] filter(FileStatus src[], PathFilter filter) {
		ArrayList<FileStatus> results = new ArrayList<>();
		for (int i = 0; i < src.length; i++) {
			if (filter.accept(src[i].getPath())) {
				results.add(src[i]);
			}
		}
		return results.toArray(new FileStatus[results.size()]);
	}

	@Override
	public FileStatus[] listStatus(Path[] files) throws FileNotFoundException, IOException {
		return doListStatuses(files);
	}

	protected FileStatus[] doListStatuses(Path[] files) throws FileNotFoundException, IOException {
		FileStatus[] res;
		try {
			res = dirCacheClient.listStatuses(files);
		} catch(Exception ex) {
			res = doDelegateListStatuses(files);
		}
		return res;
	}

	protected FileStatus[] doDelegateListStatuses(Path[] files) throws FileNotFoundException, IOException {
		List<FileStatus> res = new ArrayList<>();
		for (val file: files) {
			val tmpres = delegate.listStatus(file);
			res.addAll(Arrays.asList(tmpres));
		}
		return res.toArray(new FileStatus[res.size()]);
	}
	
	@Override
	public FileStatus[] listStatus(Path[] files, PathFilter filter) throws FileNotFoundException, IOException {
		FileStatus[] unfilteredRes = doListStatuses(files);
		return filter(unfilteredRes, filter);
	}


	@Override
	public FileStatus[] globStatus(Path pathPattern) throws IOException {
		return doGlobStatus(pathPattern);
	  }

	protected FileStatus[] doGlobStatus(Path pathPattern) throws FileNotFoundException, IOException {
		FileStatus[] res;
		try {
			res = dirCacheClient.globStatus(pathPattern);
		} catch(Exception ex) {
			res = delegate.globStatus(pathPattern);
		}
		return res;
	}
	
	@Override
	public FileStatus[] globStatus(Path pathPattern, PathFilter filter) throws IOException {
		FileStatus[] unfilteredRes = doGlobStatus(pathPattern);
		return filter(unfilteredRes, filter);
	}


	@Override
	public ContentSummary getContentSummary(Path f) throws IOException {
		// TODO (dirCacheClient not implemented yet)
		return super.getContentSummary(f);
	}


	@Override
	public RemoteIterator<FileStatus> listStatusIterator(Path p) throws FileNotFoundException, IOException {
		// TODO (dirCacheClient not implemented yet)
		return super.listStatusIterator(p);
	}

	@Override
	public RemoteIterator<LocatedFileStatus> listFiles(Path f, boolean recursive) throws FileNotFoundException, IOException {
		// TODO (dirCacheClient not implemented yet)
		return super.listFiles(f, recursive);
	}

	
	// delegate to underlying + notify dirCache of change
	// ------------------------------------------------------------------------
	
	@Override
	public FSDataOutputStream create(Path f,
		      FsPermission permission,
		      boolean overwrite,
		      int bufferSize,
		      short replication,
		      long blockSize,
		      Progressable progress) throws IOException {
		FSDataOutputStream res = super.create(f, permission, overwrite, bufferSize, replication, blockSize, progress);
		try {
			dirCacheClient.notifyCreate(f, permission, overwrite, bufferSize, replication, blockSize);
		} catch(Exception ex) {
			// ignore, no rethrow!
		}
		return res;
	}

//	  public FSDataOutputStream create(Path f,
//	      FsPermission permission,
//	      EnumSet<CreateFlag> flags,
//	      int bufferSize,
//	      short replication,
//	      long blockSize,
//	      Progressable progress,
//	      ChecksumOpt checksumOpt) throws IOException {
//	    // Checksum options are ignored by default. The file systems that
//	    // implement checksum need to override this method. The full
//	    // support is currently only available in DFS.
//	    return create(f, permission, flags.contains(CreateFlag.OVERWRITE),
//	        bufferSize, replication, blockSize, progress);
//	  }
	
//	@Override
//	public boolean mkdirs(Path f) throws IOException {
//	    return mkdirs(f, FsPermission.getDirDefault());
//	}

	@Override
	public boolean mkdirs(Path f, FsPermission permission) throws IOException {
		boolean res = super.mkdirs(f, permission);
		try {
			dirCacheClient.notifyMkdirs(f, permission);
		} catch(Exception ex) {
			// ignore, no rethrow!
		}
		return res;
	}

	@Override
	public boolean rename(Path src, Path dst) throws IOException {
		boolean res = super.rename(src, dst);
		try {
			dirCacheClient.notifyRename(src, dst);
		} catch(Exception ex) {
			// ignore, no rethrow!
		}
		return res;
	}

	@Override
	public boolean delete(Path f, boolean recursive) throws IOException {
		boolean res = super.delete(f, recursive);
		try {
			dirCacheClient.notifyDelete(f, recursive);
		} catch(Exception ex) {
			// ignore, no rethrow!
		}
		return res;
	}


	// override java.lang.Object
	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "DirCacheHadoopFileSystem{" + delegate.toString() + ", baseDirServerUrl:" + baseDirServerUrl + "}";
	}

}
