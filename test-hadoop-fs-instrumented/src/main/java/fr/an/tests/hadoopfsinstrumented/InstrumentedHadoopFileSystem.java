package fr.an.tests.hadoopfsinstrumented;

import static java.lang.System.nanoTime;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.BlockStoragePolicySpi;
import org.apache.hadoop.fs.CreateFlag;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FSDataOutputStreamBuilder;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsServerDefaults;
import org.apache.hadoop.fs.FsStatus;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.ParentNotDirectoryException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.fs.PathHandle;
import org.apache.hadoop.fs.QuotaUsage;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.StorageStatistics;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.fs.XAttrSetFlag;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.token.DelegationTokenIssuer;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.util.Progressable;

import fr.an.tests.hadoopfsinstrumented.stats.InstrumentedFSInputStreamStats;
import fr.an.tests.hadoopfsinstrumented.stats.InstrumentedFSOutputStreamStats;
import fr.an.tests.hadoopfsinstrumented.stats.InstrumentedFSPathStats;
import lombok.Getter;

/**
 * 
 *
 */
public class InstrumentedHadoopFileSystem extends FileSystem {

	private Configuration conf;

	private FileSystem delegate;
	
	@Getter
	private Statistics delegateStatistics;
	
	@Getter
	private InstrumentedFSInputStreamStats inputEntryStats = new InstrumentedFSInputStreamStats();
	@Getter
	private InstrumentedFSOutputStreamStats outputEntryStats = new InstrumentedFSOutputStreamStats();
	@Getter
	private InstrumentedFSPathStats fsPathStats = new InstrumentedFSPathStats();
	
	// --------------------------------------------------------------------------------------------

	/** constructor called by introspection, from Hadoop */
	public InstrumentedHadoopFileSystem() {
	}

	/** constructor for test, or explicit new */
	public InstrumentedHadoopFileSystem(FileSystem delegate) {
		this.delegate = delegate;
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
		
		this.statistics = super.statistics;
		
		@SuppressWarnings("deprecation")
		Statistics delegateStat = FileSystem.getStatistics(delegate.getScheme(), delegate.getClass());
		if (delegateStat == null) {
			delegateStat = statistics;
		}
		this.delegateStatistics = delegateStat;
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}

	// implements/overrides method wrapping delegate FsDataInputStream -> InstrumentedFSDataInputStream
	// ------------------------------------------------------------------------
	
	@Override
	public FSDataInputStream open(Path f, int bufferSize) throws IOException {
		long startNanos = nanoTime();
		FSDataInputStream delegateRes = delegate.open(f, bufferSize);
		long nanos = nanoTime() - startNanos;
		InstrumentedFSPathStats stats = pathStatsFor(f);
		stats.openStats.increment(nanos);
		
		// Statistics entryStatistics = getHadoopEntryStatistics(f);
		InstrumentedFSInputStreamStats inputEntryStats = inputEntryStatsFor(f);
		return new InstrumentedFSDataInputStream(delegateRes, f, inputEntryStats);
	}

//	@Override
//	public FSDataInputStream open(PathHandle fd) throws IOException {
//		return open(fd, getConf().getInt(IO_FILE_BUFFER_SIZE_KEY,
//		        IO_FILE_BUFFER_SIZE_DEFAULT));		
//	}

	@Override
	public FSDataInputStream open(PathHandle fd, int bufferSize) throws IOException {
		long startNanos = nanoTime();
		FSDataInputStream delegateRes = delegate.open(fd, bufferSize);
		long nanos = nanoTime() - startNanos;
		Path path = pathForHandle(fd);
		InstrumentedFSPathStats stats = pathStatsFor(path);
		stats.openStats.increment(nanos);

		InstrumentedFSInputStreamStats inputEntryStats = inputEntryStatsFor(path);
		return new InstrumentedFSDataInputStream(delegateRes, path, inputEntryStats); 
	}

	// implements/overrides method wrapping delegate FsDataOutputStream -> InstrumentedFSDataOutputStream
	// ------------------------------------------------------------------------
	
	@Override
	public FSDataOutputStream create(Path f, FsPermission permission, boolean overwrite, int bufferSize,
			short replication, long blockSize, Progressable progress) throws IOException {
		long startNanos = nanoTime();
		FSDataOutputStream delegateRes = delegate.create(f, permission, overwrite, bufferSize, replication, blockSize, progress);
		long nanos = nanoTime() - startNanos;
		InstrumentedFSPathStats stats = pathStatsFor(f);
		stats.createStats.increment(nanos);
		
		Statistics entryStatistics = getHadoopEntryStatistics(f);
		InstrumentedFSOutputStreamStats outputEntryStats = outputEntryStatsFor(f);
		return new InstrumentedFSDataOutputStream(delegateRes, entryStatistics, f, outputEntryStats);		
	}

	
	@Override
	public FSDataOutputStream append(Path f, int bufferSize, Progressable progress) throws IOException {
		long startNanos = nanoTime();
		FSDataOutputStream delegateRes = delegate.append(f, bufferSize, progress);
		long nanos = nanoTime() - startNanos;
		InstrumentedFSPathStats stats = pathStatsFor(f);
		stats.appendStats.increment(nanos);

		Statistics entryStatistics = getHadoopEntryStatistics(f);
		InstrumentedFSOutputStreamStats outputEntryStats = outputEntryStatsFor(f);
		return new InstrumentedFSDataOutputStream(delegateRes, entryStatistics, f, outputEntryStats);
	}

	// implements/overrides method wrapping delegate FSDataOutputStreamBuilder -> InstrumentedFSDataOutputStreamBuilder ( -> InstrumentedFSDataOutputStream) 
	// ------------------------------------------------------------------------

	@SuppressWarnings("rawtypes")
	@Override
	public FSDataOutputStreamBuilder createFile(Path path) {
		return createInstrumentedFsDataOutputStreamBuilder(path).create().overwrite(true);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public FSDataOutputStreamBuilder appendFile(Path path) {
		return createInstrumentedFsDataOutputStreamBuilder(path).append();
	}

	@Override
	public FSDataOutputStream createNonRecursive(Path f, FsPermission permission, EnumSet<CreateFlag> flags,
			int bufferSize, short replication, long blockSize, Progressable progress) throws IOException {
		long startNanos = nanoTime();
		FSDataOutputStream delegateRes = delegate.createNonRecursive(f, permission, flags, bufferSize, replication, blockSize, progress);
		long nanos = nanoTime() - startNanos;
		InstrumentedFSPathStats stats = pathStatsFor(f);
		stats.createNonRecursiveStats.increment(nanos);

		Statistics entryStatistics = getHadoopEntryStatistics(f);
		InstrumentedFSOutputStreamStats outputEntryStats = outputEntryStatsFor(f);
		return new InstrumentedFSDataOutputStream(delegateRes, entryStatistics, f, outputEntryStats);		
	}


	// implement abstract method of org.apache.hadoop.fs.FileSystem
	// --------------------------------------------------------------------------------------------
	
	@Override
	public URI getUri() {
		return delegate.getUri();
	}
	
	@Override
	public void setWorkingDirectory(Path new_dir) {
		delegate.setWorkingDirectory(new_dir);
	}

	@Override
	public Path getWorkingDirectory() {
		return delegate.getWorkingDirectory();
	}

	@Override
	public boolean rename(Path src, Path dst) throws IOException {
		long startNanos = nanoTime();
		boolean res = delegate.rename(src, dst);
		long nanos = nanoTime() - startNanos;
		InstrumentedFSPathStats stats = pathStatsFor(dst);
		stats.renameStats.increment(nanos);
		return res;
	}

	@Override
	public boolean mkdirs(Path f, FsPermission permission) throws IOException {
		long startNanos = nanoTime();
		boolean res = delegate.mkdirs(f, permission);
		long nanos = nanoTime() - startNanos;
		InstrumentedFSPathStats stats = pathStatsFor(f);
		stats.renameStats.increment(nanos);
		return res;
	}

	@Override
	public boolean delete(Path f, boolean recursive) throws IOException {
		long startNanos = nanoTime();
		boolean res = delegate.delete(f, recursive);
		long nanos = nanoTime() - startNanos;
		InstrumentedFSPathStats stats = pathStatsFor(f);
		stats.deleteStats.increment(nanos);
		return res;
	}

	@Override
	public FileStatus[] listStatus(Path f) throws FileNotFoundException, IOException {
		long startNanos = nanoTime();
		FileStatus[] res = delegate.listStatus(f);
		long nanos = nanoTime() - startNanos;
		InstrumentedFSPathStats stats = pathStatsFor(f);
		stats.listStats.increment(nanos);
		return res;
	}

	@Override
	public FileStatus getFileStatus(Path f) throws IOException {
		long startNanos = nanoTime();
		FileStatus res = delegate.getFileStatus(f);
		long nanos = nanoTime() - startNanos;
		InstrumentedFSPathStats stats = pathStatsFor(f);
		stats.queryFileStatusStats.increment(nanos);
		return res;
	}

	// override default implementation of org.apache.hadoop.fs.FileSystem
	// --------------------------------------------------------------------------------------------

	// @Override // ?? bug in eclipse compiler
	public Token<?>[] addDelegationTokens(String renewer, Credentials credentials) throws IOException {
		return delegate.addDelegationTokens(renewer, credentials);
	}

	@Override
	public String getScheme() {
		return delegate.getScheme();
	}

	@Override
	public String getCanonicalServiceName() {
		return delegate.getCanonicalServiceName();
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public Path makeQualified(Path path) {
		return delegate.makeQualified(path);
	}

	@Override
	public Token<?> getDelegationToken(String renewer) throws IOException {
		long startNanos = nanoTime();
		Token<?> res = delegate.getDelegationToken(renewer);
		long nanos = nanoTime() - startNanos;
		fsPathStats.delegationTokenStats.increment(nanos);
		return res;
	}

	@Override
	public FileSystem[] getChildFileSystems() {
		return delegate.getChildFileSystems();
	}

	@Override
	public DelegationTokenIssuer[] getAdditionalTokenIssuers() throws IOException {
		return delegate.getAdditionalTokenIssuers();
	}

	@Override
	public BlockLocation[] getFileBlockLocations(FileStatus file, long start, long len) throws IOException {
		return delegate.getFileBlockLocations(file, start, len);
	}

	@Override
	public BlockLocation[] getFileBlockLocations(Path p, long start, long len) throws IOException {
		return delegate.getFileBlockLocations(p, start, len);
	}

	@SuppressWarnings("deprecation")
	@Override
	public FsServerDefaults getServerDefaults() throws IOException {
		return delegate.getServerDefaults();
	}

	@Override
	public FsServerDefaults getServerDefaults(Path p) throws IOException {
		return delegate.getServerDefaults(p);
	}

	@Override
	public Path resolvePath(Path p) throws IOException {
		return delegate.resolvePath(p);
	}

//	@Override
//	public FSDataInputStream open(Path f) throws IOException {
//		long startNanos = nanoTime();
//		// return open(f, getConf().getInt(IO_FILE_BUFFER_SIZE_KEY, IO_FILE_BUFFER_SIZE_DEFAULT));
//		FSDataInputStream res = super.open(f); 
//		long nanos = nanoTime() - startNanos;
//		InstrumentedFSPathStats stats = pathStatsFor(f);
//		stats.openStats.increment(nanos);
//		return res;
//	}
//	  
//	@Override
//	public FSDataOutputStream create(Path f) throws IOException {
//		// return create(f, true);
//		return super.create(f);
//	}
//
//	@Override
//	public FSDataOutputStream create(Path f, boolean overwrite) throws IOException {
//		// return create(f, overwrite, getConf().getInt(IO_FILE_BUFFER_SIZE_KEY, IO_FILE_BUFFER_SIZE_DEFAULT), getDefaultReplication(f), getDefaultBlockSize(f));
//		return super.create(f, overwrite);
//	}
//
//	@Override
//	public FSDataOutputStream create(Path f, Progressable progress) throws IOException {
//		// return create(f, true, getConf().getInt(IO_FILE_BUFFER_SIZE_KEY, IO_FILE_BUFFER_SIZE_DEFAULT), getDefaultReplication(f), getDefaultBlockSize(f), progress);
//		return super.create(f, progress);
//	}
//
//	@Override
//	public FSDataOutputStream create(Path f, short replication) throws IOException {
//		// return create(f, true, getConf().getInt(IO_FILE_BUFFER_SIZE_KEY, IO_FILE_BUFFER_SIZE_DEFAULT), replication, getDefaultBlockSize(f));
//		return super.create(f, replication);
//	}
//
//	@Override
//	public FSDataOutputStream create(Path f, short replication, Progressable progress) throws IOException {
//		// return create(f, true, getConf().getInt(IO_FILE_BUFFER_SIZE_KEY, IO_FILE_BUFFER_SIZE_DEFAULT), replication, getDefaultBlockSize(f), progress);
//		return super.create(f, replication, progress);
//	}
//
//	@Override
//	public FSDataOutputStream create(Path f, boolean overwrite, int bufferSize) throws IOException {
//		// return create(f, overwrite, bufferSize, getDefaultReplication(f), getDefaultBlockSize(f));
//		return super.create(f, overwrite, bufferSize);
//	}
//
//	@Override
//	public FSDataOutputStream create(Path f, boolean overwrite, int bufferSize, Progressable progress) throws IOException {
//		// return create(f, overwrite, bufferSize, getDefaultReplication(f), getDefaultBlockSize(f), progress);
//		return super.create(f, overwrite, bufferSize, progress);
//	}
//
//	@Override
//	public FSDataOutputStream create(Path f, boolean overwrite, int bufferSize, short replication, long blockSize) throws IOException {
//		return super.create(f, overwrite, bufferSize, replication, blockSize);
//	}
//
//	@Override
//	public FSDataOutputStream create(Path f, boolean overwrite, int bufferSize, short replication, long blockSize,
//			Progressable progress) throws IOException {
//		return super.create(f, overwrite, bufferSize, replication, blockSize, progress);
//	}
//
//	@Override
//	public FSDataOutputStream create(Path f, FsPermission permission, EnumSet<CreateFlag> flags, int bufferSize,
//			short replication, long blockSize, Progressable progress) throws IOException {
//		return super.create(f, permission, flags, bufferSize, replication, blockSize, progress);
//	}
//
//	@Override
//	public FSDataOutputStream create(Path f, 
//			FsPermission permission, EnumSet<CreateFlag> flags, int bufferSize,
//			short replication, long blockSize, Progressable progress, ChecksumOpt checksumOpt) throws IOException {
//		return super.create(f, permission, flags, bufferSize, replication, blockSize, progress, checksumOpt);
//	}
//
//	@Override
//	public FSDataOutputStream createNonRecursive(Path f, 
//			boolean overwrite, int bufferSize, short replication,
//			long blockSize, Progressable progress) throws IOException {
//		return super.createNonRecursive(f, overwrite, bufferSize, replication, blockSize, progress);
//	}
//
//	@Override
//	public FSDataOutputStream createNonRecursive(Path f, FsPermission permission, boolean overwrite, int bufferSize,
//			short replication, long blockSize, Progressable progress) throws IOException {
//		return super.createNonRecursive(f, permission, overwrite, bufferSize, replication, blockSize, progress);
//	}

	@Override
	public boolean createNewFile(Path f) throws IOException {
		return delegate.createNewFile(f);
	}

//	@Override
//	public FSDataOutputStream append(Path f) throws IOException {
//		return append(f, getConf().getInt(IO_FILE_BUFFER_SIZE_KEY, IO_FILE_BUFFER_SIZE_DEFAULT), null);
//	}
//
//	@Override
//	public FSDataOutputStream append(Path f, int bufferSize) throws IOException {
//		return append(f, bufferSize, null);
//	}

	@Override
	public void concat(Path trg, Path[] psrcs) throws IOException {
		long startNanos = nanoTime();
		delegate.concat(trg, psrcs);
		long nanos = nanoTime() - startNanos;
		InstrumentedFSPathStats stats = pathStatsFor(trg);
		stats.concatStats.increment(nanos);
	}

	@SuppressWarnings("deprecation")
	@Override
	public short getReplication(Path src) throws IOException {
		return delegate.getReplication(src);
	}

	@Override
	public boolean setReplication(Path src, short replication) throws IOException {
		return delegate.setReplication(src, replication);
	}

	@Override
	public boolean truncate(Path f, long newLength) throws IOException {
		long startNanos = nanoTime();
		boolean res = delegate.truncate(f, newLength);
		long nanos = nanoTime() - startNanos;
		InstrumentedFSPathStats stats = pathStatsFor(f);
		stats.truncateStats.increment(nanos);
		return res;
	}

//	@SuppressWarnings("deprecation")
//	@Override
//	public boolean delete(Path f) throws IOException {
//		return delete(f, true);
//	}

	@Override
	public boolean deleteOnExit(Path f) throws IOException {
		return delegate.deleteOnExit(f);
	}

	@Override
	public boolean cancelDeleteOnExit(Path f) {
		return delegate.cancelDeleteOnExit(f);
	}

//	@Override
//	public boolean exists(Path f) throws IOException {
//		// => getFileStatus
//		return delegate.exists(f);
//	}
//
//	@SuppressWarnings("deprecation")
//	@Override
//	public boolean isDirectory(Path f) throws IOException {
//		// => getFileStatus
//		return delegate.isDirectory(f);
//	}
//
//	@SuppressWarnings("deprecation")
//	@Override
//	public boolean isFile(Path f) throws IOException {
//		// => getFileStatus
//		return delegate.isFile(f);
//	}
//
//	@SuppressWarnings("deprecation")
//	@Override
//	public long getLength(Path f) throws IOException {
//		// => getFileStatus
//		return delegate.getLength(f);
//	}
//
//	@Override
//	public ContentSummary getContentSummary(Path f) throws IOException {
//		// => getFileStatus
//		return delegate.getContentSummary(f);
//	}

	@Override
	public QuotaUsage getQuotaUsage(Path f) throws IOException {
		return delegate.getQuotaUsage(f);
	}

	@Override
	public RemoteIterator<Path> listCorruptFileBlocks(Path path) throws IOException {
		return delegate.listCorruptFileBlocks(path);
	}

	@Override
	public FileStatus[] listStatus(Path f, PathFilter filter) throws FileNotFoundException, IOException {
		long startNanos = nanoTime();
		// => listStatus(results, f, filter);
		FileStatus[] res = delegate.listStatus(f, filter);
		long nanos = nanoTime() - startNanos;
		InstrumentedFSPathStats stats = pathStatsFor(f);
		stats.listStatusFilterStats.increment(nanos);
		return res;
	}

	@Override
	public FileStatus[] listStatus(Path[] files) throws FileNotFoundException, IOException {
		long startNanos = nanoTime();
		FileStatus[] res = delegate.listStatus(files);
		long nanos = nanoTime() - startNanos;
		fsPathStats.listStatusMultiStats.increment(nanos);
		return res;
	}

	@Override
	public FileStatus[] listStatus(Path[] files, PathFilter filter) throws FileNotFoundException, IOException {
		long startNanos = nanoTime();
		FileStatus[] res = delegate.listStatus(files, filter);
		long nanos = nanoTime() - startNanos;
		fsPathStats.listStatusFilterMultiStats.increment(nanos);
		return res;
	}

	@Override
	public FileStatus[] globStatus(Path pathPattern) throws IOException {
		long startNanos = nanoTime();
		FileStatus[] res = delegate.globStatus(pathPattern);
		long nanos = nanoTime() - startNanos;
		fsPathStats.globStats.increment(nanos);
		return res;
	}

	@Override
	public FileStatus[] globStatus(Path pathPattern, PathFilter filter) throws IOException {
		long startNanos = nanoTime();
		FileStatus[] res = delegate.globStatus(pathPattern, filter);
		long nanos = nanoTime() - startNanos;
		fsPathStats.globFilterStats.increment(nanos);
		return res;
	}

	@Override
	public RemoteIterator<LocatedFileStatus> listLocatedStatus(Path f) throws FileNotFoundException, IOException {
		long startNanos = nanoTime();
		RemoteIterator<LocatedFileStatus> res = delegate.listLocatedStatus(f);
		long nanos = nanoTime() - startNanos;
		fsPathStats.listLocatedStatusStats.increment(nanos);
		return res;
	}

	@Override
	public RemoteIterator<FileStatus> listStatusIterator(Path p) throws FileNotFoundException, IOException {
		long startNanos = nanoTime();
		RemoteIterator<FileStatus> res = delegate.listStatusIterator(p);
		long nanos = nanoTime() - startNanos;
		fsPathStats.listStatusIteratorStats.increment(nanos);
		return res;
	}

	@Override
	public RemoteIterator<LocatedFileStatus> listFiles(Path f, boolean recursive) throws FileNotFoundException, IOException {
		long startNanos = nanoTime();
		RemoteIterator<LocatedFileStatus> res = delegate.listFiles(f, recursive);
		long nanos = nanoTime() - startNanos;
		fsPathStats.listLocatedFilesStats.increment(nanos);
		return res;
	}

	@Override
	public Path getHomeDirectory() {
		return delegate.getHomeDirectory();
	}
	
	@Override
	public boolean mkdirs(Path f) throws IOException {
		return delegate.mkdirs(f);
	}

	@Override
	public void copyFromLocalFile(Path src, Path dst) throws IOException {
		delegate.copyFromLocalFile(src, dst);
	}

	@Override
	public void moveFromLocalFile(Path[] srcs, Path dst) throws IOException {
		delegate.moveFromLocalFile(srcs, dst);
	}

	@Override
	public void moveFromLocalFile(Path src, Path dst) throws IOException {
		delegate.moveFromLocalFile(src, dst);
	}

	@Override
	public void copyFromLocalFile(boolean delSrc, Path src, Path dst) throws IOException {
		delegate.copyFromLocalFile(delSrc, src, dst);
	}

	@Override
	public void copyFromLocalFile(boolean delSrc, boolean overwrite, Path[] srcs, Path dst) throws IOException {
		delegate.copyFromLocalFile(delSrc, overwrite, srcs, dst);
	}

	@Override
	public void copyFromLocalFile(boolean delSrc, boolean overwrite, Path src, Path dst) throws IOException {
		delegate.copyFromLocalFile(delSrc, overwrite, src, dst);
	}

	@Override
	public void copyToLocalFile(Path src, Path dst) throws IOException {
		delegate.copyToLocalFile(src, dst);
	}

	@Override
	public void moveToLocalFile(Path src, Path dst) throws IOException {
		delegate.moveToLocalFile(src, dst);
	}

	@Override
	public void copyToLocalFile(boolean delSrc, Path src, Path dst) throws IOException {
		delegate.copyToLocalFile(delSrc, src, dst);
	}

	@Override
	public void copyToLocalFile(boolean delSrc, Path src, Path dst, boolean useRawLocalFileSystem) throws IOException {
		delegate.copyToLocalFile(delSrc, src, dst, useRawLocalFileSystem);
	}

	@Override
	public Path startLocalOutput(Path fsOutputFile, Path tmpLocalFile) throws IOException {
		return delegate.startLocalOutput(fsOutputFile, tmpLocalFile);
	}

	@Override
	public void completeLocalOutput(Path fsOutputFile, Path tmpLocalFile) throws IOException {
		delegate.completeLocalOutput(fsOutputFile, tmpLocalFile);
	}

	@Override
	public long getUsed() throws IOException {
		return delegate.getUsed();
	}

	@Override
	public long getUsed(Path path) throws IOException {
		return delegate.getUsed(path);
	}

	@SuppressWarnings("deprecation")
	@Override
	public long getBlockSize(Path f) throws IOException {
		return delegate.getBlockSize(f);
	}

	@SuppressWarnings("deprecation")
	@Override
	public long getDefaultBlockSize() {
		return delegate.getDefaultBlockSize();
	}

	@Override
	public long getDefaultBlockSize(Path f) {
		return delegate.getDefaultBlockSize(f);
	}

	@SuppressWarnings("deprecation")
	@Override
	public short getDefaultReplication() {
		return delegate.getDefaultReplication();
	}

	@Override
	public short getDefaultReplication(Path path) {
		return delegate.getDefaultReplication(path);
	}

	@Override
	public void access(Path path, FsAction mode) throws AccessControlException, FileNotFoundException, IOException {
		delegate.access(path, mode);
	}

	@Override
	public void createSymlink(Path target, Path link, boolean createParent)
			throws AccessControlException, FileAlreadyExistsException, FileNotFoundException,
			ParentNotDirectoryException, UnsupportedFileSystemException, IOException {
		delegate.createSymlink(target, link, createParent);
	}

	@Override
	public FileStatus getFileLinkStatus(Path f)
			throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
		return delegate.getFileLinkStatus(f);
	}

	@Override
	public boolean supportsSymlinks() {
		return delegate.supportsSymlinks();
	}

	@Override
	public Path getLinkTarget(Path f) throws IOException {
		return delegate.getLinkTarget(f);
	}

	@Override
	public FileChecksum getFileChecksum(Path f) throws IOException {
		long startNanos = nanoTime();
		FileChecksum res = delegate.getFileChecksum(f);
		long nanos = nanoTime() - startNanos;
		fsPathStats.getFileChecksumStats.increment(nanos);
		return res;
	}

	@Override
	public FileChecksum getFileChecksum(Path f, long length) throws IOException {
		long startNanos = nanoTime();
		FileChecksum res = delegate.getFileChecksum(f, length);
		long nanos = nanoTime() - startNanos;
		fsPathStats.getFileChecksumLenStats.increment(nanos);
		return res;
	}

	@Override
	public void setVerifyChecksum(boolean verifyChecksum) {
		delegate.setVerifyChecksum(verifyChecksum);
	}

	@Override
	public void setWriteChecksum(boolean writeChecksum) {
		delegate.setWriteChecksum(writeChecksum);
	}

	@Override
	public FsStatus getStatus() throws IOException {
		long startNanos = nanoTime();
		FsStatus res = delegate.getStatus();
		long nanos = nanoTime() - startNanos;
		fsPathStats.getStatusStats.increment(nanos);
		return res;
	}

	@Override
	public FsStatus getStatus(Path p) throws IOException {
		long startNanos = nanoTime();
		FsStatus res = delegate.getStatus(p);
		long nanos = nanoTime() - startNanos;
		InstrumentedFSPathStats stats = pathStatsFor(p);
		stats.getStatusStats.increment(nanos);
		return res;
	}

	@Override
	public void setPermission(Path p, FsPermission permission) throws IOException {
		delegate.setPermission(p, permission);
	}

	@Override
	public void setOwner(Path p, String username, String groupname) throws IOException {
		delegate.setOwner(p, username, groupname);
	}

	@Override
	public void setTimes(Path p, long mtime, long atime) throws IOException {
		delegate.setTimes(p, mtime, atime);
	}

	@Override
	public Path createSnapshot(Path path, String snapshotName) throws IOException {
		return delegate.createSnapshot(path, snapshotName);
	}

	@Override
	public void renameSnapshot(Path path, String snapshotOldName, String snapshotNewName) throws IOException {
		delegate.renameSnapshot(path, snapshotOldName, snapshotNewName);
	}

	@Override
	public void deleteSnapshot(Path path, String snapshotName) throws IOException {
		delegate.deleteSnapshot(path, snapshotName);
	}

	@Override
	public void modifyAclEntries(Path path, List<AclEntry> aclSpec) throws IOException {
		delegate.modifyAclEntries(path, aclSpec);
	}

	@Override
	public void removeAclEntries(Path path, List<AclEntry> aclSpec) throws IOException {
		delegate.removeAclEntries(path, aclSpec);
	}

	@Override
	public void removeDefaultAcl(Path path) throws IOException {
		delegate.removeDefaultAcl(path);
	}

	@Override
	public void removeAcl(Path path) throws IOException {
		delegate.removeAcl(path);
	}

	@Override
	public void setAcl(Path path, List<AclEntry> aclSpec) throws IOException {
		delegate.setAcl(path, aclSpec);
	}

	@Override
	public AclStatus getAclStatus(Path path) throws IOException {
		return delegate.getAclStatus(path);
	}

	@Override
	public void setXAttr(Path path, String name, byte[] value) throws IOException {
		delegate.setXAttr(path, name, value);
	}

	@Override
	public void setXAttr(Path path, String name, byte[] value, EnumSet<XAttrSetFlag> flag) throws IOException {
		delegate.setXAttr(path, name, value, flag);
	}

	@Override
	public byte[] getXAttr(Path path, String name) throws IOException {
		return delegate.getXAttr(path, name);
	}

	@Override
	public Map<String, byte[]> getXAttrs(Path path) throws IOException {
		return delegate.getXAttrs(path);
	}

	@Override
	public Map<String, byte[]> getXAttrs(Path path, List<String> names) throws IOException {
		return delegate.getXAttrs(path, names);
	}

	@Override
	public List<String> listXAttrs(Path path) throws IOException {
		return delegate.listXAttrs(path);
	}

	@Override
	public void removeXAttr(Path path, String name) throws IOException {
		delegate.removeXAttr(path, name);
	}

	@Override
	public void setStoragePolicy(Path src, String policyName) throws IOException {
		delegate.setStoragePolicy(src, policyName);
	}

	@Override
	public void unsetStoragePolicy(Path src) throws IOException {
		delegate.unsetStoragePolicy(src);
	}

	@Override
	public BlockStoragePolicySpi getStoragePolicy(Path src) throws IOException {
		return delegate.getStoragePolicy(src);
	}

	@Override
	public Collection<? extends BlockStoragePolicySpi> getAllStoragePolicies() throws IOException {
		return delegate.getAllStoragePolicies();
	}

	@Override
	public Path getTrashRoot(Path path) {
		return delegate.getTrashRoot(path);
	}

	@Override
	public Collection<FileStatus> getTrashRoots(boolean allUsers) {
		return delegate.getTrashRoots(allUsers);
	}

	@Override
	public StorageStatistics getStorageStatistics() {
		return delegate.getStorageStatistics();
	}

	// override java.lang.Object
	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "InstrumentedFS{" + delegate.toString() + "}";
	}


	private Path pathForHandle(PathHandle fd) {
		Path path = new Path("/dummyPathForHandle"); // TODO
		return path;
	}


	protected InstrumentedFSDataOutputStreamBuilder createInstrumentedFsDataOutputStreamBuilder(Path path) {
		long startNanos = nanoTime();
		FSDataOutputStreamBuilder<?,?> delegateRes = delegate.createFile(path);
		long nanos = nanoTime() - startNanos;
		InstrumentedFSPathStats stats = pathStatsFor(path);
		stats.createStats.increment(nanos);
		
		InstrumentedFSOutputStreamStats outputEntryStats = outputEntryStatsFor(path);
		return new InstrumentedFSDataOutputStreamBuilder(this, path, delegateRes, outputEntryStats);
	}


	// ------------------------------------------------------------------------

	public Statistics getGlobalStatistics() {
		return statistics;
	}
	
	public Statistics getHadoopEntryStatistics(Path f) {
		// TOCHANGE
		return statistics;
	}

	protected InstrumentedFSInputStreamStats inputEntryStatsFor(Path path) {
		// TODO
		return inputEntryStats;
	}

	protected InstrumentedFSOutputStreamStats outputEntryStatsFor(Path path) {
		// TODO 
		return outputEntryStats;
	}
	
	protected InstrumentedFSPathStats pathStatsFor(Path path) {
		// TODO 
		return fsPathStats;
	}

	
}
