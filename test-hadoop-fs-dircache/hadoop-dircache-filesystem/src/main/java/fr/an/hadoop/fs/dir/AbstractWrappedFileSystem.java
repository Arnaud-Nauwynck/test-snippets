package fr.an.hadoop.fs.dir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.BlockStoragePolicySpi;
import org.apache.hadoop.fs.ContentSummary;
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
import org.apache.hadoop.fs.FutureDataInputStreamBuilder;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.MultipartUploaderBuilder;
import org.apache.hadoop.fs.Options.ChecksumOpt;
import org.apache.hadoop.fs.ParentNotDirectoryException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.fs.PathHandle;
import org.apache.hadoop.fs.QuotaUsage;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.StorageStatistics;
import org.apache.hadoop.fs.StorageType;
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

public abstract class AbstractWrappedFileSystem extends FileSystem {

	protected FileSystem delegate;

	// ------------------------------------------------------------------------
	
	protected AbstractWrappedFileSystem() {
	}
	
	// ------------------------------------------------------------------------

	protected FileSystem getDelegate() {
		return delegate;
	}

	protected void setDelegate(FileSystem delegate) {
		this.delegate = delegate;
	}

	
	// delegat eall methods to <code>delegate</code>
	// ------------------------------------------------------------------------
	
	public void setConf(Configuration conf) {
		delegate.setConf(conf);
	}
	public Configuration getConf() {
		return delegate.getConf();
	}

	public Token<?>[] addDelegationTokens(String renewer, Credentials credentials) throws IOException {
		return delegate.addDelegationTokens(renewer, credentials);
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	public String toString() {
		return delegate.toString();
	}

	public void initialize(URI name, Configuration conf) throws IOException {
		delegate.initialize(name, conf);
	}

	public String getScheme() {
		return delegate.getScheme();
	}

	public URI getUri() {
		return delegate.getUri();
	}

	public String getCanonicalServiceName() {
		return delegate.getCanonicalServiceName();
	}

	@SuppressWarnings("deprecation")
	public String getName() {
		return delegate.getName();
	}

	public Path makeQualified(Path path) {
		return delegate.makeQualified(path);
	}

	public Token<?> getDelegationToken(String renewer) throws IOException {
		return delegate.getDelegationToken(renewer);
	}

	public FileSystem[] getChildFileSystems() {
		return delegate.getChildFileSystems();
	}

	public DelegationTokenIssuer[] getAdditionalTokenIssuers() throws IOException {
		return delegate.getAdditionalTokenIssuers();
	}

	public BlockLocation[] getFileBlockLocations(FileStatus file, long start, long len) throws IOException {
		return delegate.getFileBlockLocations(file, start, len);
	}

	public BlockLocation[] getFileBlockLocations(Path p, long start, long len) throws IOException {
		return delegate.getFileBlockLocations(p, start, len);
	}

	@SuppressWarnings("deprecation")
	public FsServerDefaults getServerDefaults() throws IOException {
		return delegate.getServerDefaults();
	}

	public FsServerDefaults getServerDefaults(Path p) throws IOException {
		return delegate.getServerDefaults(p);
	}

	public Path resolvePath(Path p) throws IOException {
		return delegate.resolvePath(p);
	}

	public FSDataInputStream open(Path f, int bufferSize) throws IOException {
		return delegate.open(f, bufferSize);
	}

	public FSDataInputStream open(Path f) throws IOException {
		return delegate.open(f);
	}

	public FSDataInputStream open(PathHandle fd) throws IOException {
		return delegate.open(fd);
	}

	public FSDataInputStream open(PathHandle fd, int bufferSize) throws IOException {
		return delegate.open(fd, bufferSize);
	}

	public FSDataOutputStream create(Path f) throws IOException {
		return delegate.create(f);
	}

	public FSDataOutputStream create(Path f, boolean overwrite) throws IOException {
		return delegate.create(f, overwrite);
	}

	public FSDataOutputStream create(Path f, Progressable progress) throws IOException {
		return delegate.create(f, progress);
	}

	public FSDataOutputStream create(Path f, short replication) throws IOException {
		return delegate.create(f, replication);
	}

	public FSDataOutputStream create(Path f, short replication, Progressable progress) throws IOException {
		return delegate.create(f, replication, progress);
	}

	public FSDataOutputStream create(Path f, boolean overwrite, int bufferSize) throws IOException {
		return delegate.create(f, overwrite, bufferSize);
	}

	public FSDataOutputStream create(Path f, boolean overwrite, int bufferSize, Progressable progress)
			throws IOException {
		return delegate.create(f, overwrite, bufferSize, progress);
	}

	public FSDataOutputStream create(Path f, boolean overwrite, int bufferSize, short replication, long blockSize)
			throws IOException {
		return delegate.create(f, overwrite, bufferSize, replication, blockSize);
	}

	public FSDataOutputStream create(Path f, boolean overwrite, int bufferSize, short replication, long blockSize,
			Progressable progress) throws IOException {
		return delegate.create(f, overwrite, bufferSize, replication, blockSize, progress);
	}

	public FSDataOutputStream create(Path f, FsPermission permission, boolean overwrite, int bufferSize,
			short replication, long blockSize, Progressable progress) throws IOException {
		return delegate.create(f, permission, overwrite, bufferSize, replication, blockSize, progress);
	}

	public FSDataOutputStream create(Path f, FsPermission permission, EnumSet<CreateFlag> flags, int bufferSize,
			short replication, long blockSize, Progressable progress) throws IOException {
		return delegate.create(f, permission, flags, bufferSize, replication, blockSize, progress);
	}

	public FSDataOutputStream create(Path f, FsPermission permission, EnumSet<CreateFlag> flags, int bufferSize,
			short replication, long blockSize, Progressable progress, ChecksumOpt checksumOpt) throws IOException {
		return delegate.create(f, permission, flags, bufferSize, replication, blockSize, progress, checksumOpt);
	}

	public FSDataOutputStream createNonRecursive(Path f, boolean overwrite, int bufferSize, short replication,
			long blockSize, Progressable progress) throws IOException {
		return delegate.createNonRecursive(f, overwrite, bufferSize, replication, blockSize, progress);
	}

	public FSDataOutputStream createNonRecursive(Path f, FsPermission permission, boolean overwrite, int bufferSize,
			short replication, long blockSize, Progressable progress) throws IOException {
		return delegate.createNonRecursive(f, permission, overwrite, bufferSize, replication, blockSize, progress);
	}

	public FSDataOutputStream createNonRecursive(Path f, FsPermission permission, EnumSet<CreateFlag> flags,
			int bufferSize, short replication, long blockSize, Progressable progress) throws IOException {
		return delegate.createNonRecursive(f, permission, flags, bufferSize, replication, blockSize, progress);
	}

	public boolean createNewFile(Path f) throws IOException {
		return delegate.createNewFile(f);
	}

	public FSDataOutputStream append(Path f) throws IOException {
		return delegate.append(f);
	}

	public FSDataOutputStream append(Path f, int bufferSize) throws IOException {
		return delegate.append(f, bufferSize);
	}

	public FSDataOutputStream append(Path f, int bufferSize, Progressable progress) throws IOException {
		return delegate.append(f, bufferSize, progress);
	}

	public void concat(Path trg, Path[] psrcs) throws IOException {
		delegate.concat(trg, psrcs);
	}

	@SuppressWarnings("deprecation")
	public short getReplication(Path src) throws IOException {
		return delegate.getReplication(src);
	}

	public boolean setReplication(Path src, short replication) throws IOException {
		return delegate.setReplication(src, replication);
	}

	public boolean rename(Path src, Path dst) throws IOException {
		return delegate.rename(src, dst);
	}

	public boolean truncate(Path f, long newLength) throws IOException {
		return delegate.truncate(f, newLength);
	}

	@SuppressWarnings("deprecation")
	public boolean delete(Path f) throws IOException {
		return delegate.delete(f);
	}

	public boolean delete(Path f, boolean recursive) throws IOException {
		return delegate.delete(f, recursive);
	}

	public boolean deleteOnExit(Path f) throws IOException {
		return delegate.deleteOnExit(f);
	}

	public boolean cancelDeleteOnExit(Path f) {
		return delegate.cancelDeleteOnExit(f);
	}

	public boolean exists(Path f) throws IOException {
		return delegate.exists(f);
	}

	@SuppressWarnings("deprecation")
	public boolean isDirectory(Path f) throws IOException {
		return delegate.isDirectory(f);
	}

	@SuppressWarnings("deprecation")
	public boolean isFile(Path f) throws IOException {
		return delegate.isFile(f);
	}

	@SuppressWarnings("deprecation")
	public long getLength(Path f) throws IOException {
		return delegate.getLength(f);
	}

	public ContentSummary getContentSummary(Path f) throws IOException {
		return delegate.getContentSummary(f);
	}

	public QuotaUsage getQuotaUsage(Path f) throws IOException {
		return delegate.getQuotaUsage(f);
	}

	public void setQuota(Path src, long namespaceQuota, long storagespaceQuota) throws IOException {
		delegate.setQuota(src, namespaceQuota, storagespaceQuota);
	}

	public void setQuotaByStorageType(Path src, StorageType type, long quota) throws IOException {
		delegate.setQuotaByStorageType(src, type, quota);
	}

	public FileStatus[] listStatus(Path f) throws FileNotFoundException, IOException {
		return delegate.listStatus(f);
	}

	public RemoteIterator<Path> listCorruptFileBlocks(Path path) throws IOException {
		return delegate.listCorruptFileBlocks(path);
	}

	public FileStatus[] listStatus(Path f, PathFilter filter) throws FileNotFoundException, IOException {
		return delegate.listStatus(f, filter);
	}

	public FileStatus[] listStatus(Path[] files) throws FileNotFoundException, IOException {
		return delegate.listStatus(files);
	}

	public FileStatus[] listStatus(Path[] files, PathFilter filter) throws FileNotFoundException, IOException {
		return delegate.listStatus(files, filter);
	}

	public FileStatus[] globStatus(Path pathPattern) throws IOException {
		return delegate.globStatus(pathPattern);
	}

	public FileStatus[] globStatus(Path pathPattern, PathFilter filter) throws IOException {
		return delegate.globStatus(pathPattern, filter);
	}

	public RemoteIterator<LocatedFileStatus> listLocatedStatus(Path f) throws FileNotFoundException, IOException {
		return delegate.listLocatedStatus(f);
	}

	public RemoteIterator<FileStatus> listStatusIterator(Path p) throws FileNotFoundException, IOException {
		return delegate.listStatusIterator(p);
	}

	public RemoteIterator<LocatedFileStatus> listFiles(Path f, boolean recursive)
			throws FileNotFoundException, IOException {
		return delegate.listFiles(f, recursive);
	}

	public Path getHomeDirectory() {
		return delegate.getHomeDirectory();
	}

	public void setWorkingDirectory(Path new_dir) {
		delegate.setWorkingDirectory(new_dir);
	}

	public Path getWorkingDirectory() {
		return delegate.getWorkingDirectory();
	}

	public boolean mkdirs(Path f) throws IOException {
		return delegate.mkdirs(f);
	}

	public boolean mkdirs(Path f, FsPermission permission) throws IOException {
		return delegate.mkdirs(f, permission);
	}

	public void copyFromLocalFile(Path src, Path dst) throws IOException {
		delegate.copyFromLocalFile(src, dst);
	}

	public void moveFromLocalFile(Path[] srcs, Path dst) throws IOException {
		delegate.moveFromLocalFile(srcs, dst);
	}

	public void moveFromLocalFile(Path src, Path dst) throws IOException {
		delegate.moveFromLocalFile(src, dst);
	}

	public void copyFromLocalFile(boolean delSrc, Path src, Path dst) throws IOException {
		delegate.copyFromLocalFile(delSrc, src, dst);
	}

	public void copyFromLocalFile(boolean delSrc, boolean overwrite, Path[] srcs, Path dst) throws IOException {
		delegate.copyFromLocalFile(delSrc, overwrite, srcs, dst);
	}

	public void copyFromLocalFile(boolean delSrc, boolean overwrite, Path src, Path dst) throws IOException {
		delegate.copyFromLocalFile(delSrc, overwrite, src, dst);
	}

	public void copyToLocalFile(Path src, Path dst) throws IOException {
		delegate.copyToLocalFile(src, dst);
	}

	public void moveToLocalFile(Path src, Path dst) throws IOException {
		delegate.moveToLocalFile(src, dst);
	}

	public void copyToLocalFile(boolean delSrc, Path src, Path dst) throws IOException {
		delegate.copyToLocalFile(delSrc, src, dst);
	}

	public void copyToLocalFile(boolean delSrc, Path src, Path dst, boolean useRawLocalFileSystem) throws IOException {
		delegate.copyToLocalFile(delSrc, src, dst, useRawLocalFileSystem);
	}

	public Path startLocalOutput(Path fsOutputFile, Path tmpLocalFile) throws IOException {
		return delegate.startLocalOutput(fsOutputFile, tmpLocalFile);
	}

	public void completeLocalOutput(Path fsOutputFile, Path tmpLocalFile) throws IOException {
		delegate.completeLocalOutput(fsOutputFile, tmpLocalFile);
	}

	public void close() throws IOException {
		delegate.close();
	}

	public long getUsed() throws IOException {
		return delegate.getUsed();
	}

	public long getUsed(Path path) throws IOException {
		return delegate.getUsed(path);
	}

	@SuppressWarnings("deprecation")
	public long getBlockSize(Path f) throws IOException {
		return delegate.getBlockSize(f);
	}

	@SuppressWarnings("deprecation")
	public long getDefaultBlockSize() {
		return delegate.getDefaultBlockSize();
	}

	public long getDefaultBlockSize(Path f) {
		return delegate.getDefaultBlockSize(f);
	}

	@SuppressWarnings("deprecation")
	public short getDefaultReplication() {
		return delegate.getDefaultReplication();
	}

	public short getDefaultReplication(Path path) {
		return delegate.getDefaultReplication(path);
	}

	public FileStatus getFileStatus(Path f) throws IOException {
		return delegate.getFileStatus(f);
	}

	public void msync() throws IOException, UnsupportedOperationException {
		delegate.msync();
	}

	public void access(Path path, FsAction mode) throws AccessControlException, FileNotFoundException, IOException {
		delegate.access(path, mode);
	}

	public void createSymlink(Path target, Path link, boolean createParent)
			throws AccessControlException, FileAlreadyExistsException, FileNotFoundException,
			ParentNotDirectoryException, UnsupportedFileSystemException, IOException {
		delegate.createSymlink(target, link, createParent);
	}

	public FileStatus getFileLinkStatus(Path f)
			throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
		return delegate.getFileLinkStatus(f);
	}

	public boolean supportsSymlinks() {
		return delegate.supportsSymlinks();
	}

	public Path getLinkTarget(Path f) throws IOException {
		return delegate.getLinkTarget(f);
	}

	public FileChecksum getFileChecksum(Path f) throws IOException {
		return delegate.getFileChecksum(f);
	}

	public FileChecksum getFileChecksum(Path f, long length) throws IOException {
		return delegate.getFileChecksum(f, length);
	}

	public void setVerifyChecksum(boolean verifyChecksum) {
		delegate.setVerifyChecksum(verifyChecksum);
	}

	public void setWriteChecksum(boolean writeChecksum) {
		delegate.setWriteChecksum(writeChecksum);
	}

	public FsStatus getStatus() throws IOException {
		return delegate.getStatus();
	}

	public FsStatus getStatus(Path p) throws IOException {
		return delegate.getStatus(p);
	}

	public void setPermission(Path p, FsPermission permission) throws IOException {
		delegate.setPermission(p, permission);
	}

	public void setOwner(Path p, String username, String groupname) throws IOException {
		delegate.setOwner(p, username, groupname);
	}

	public void setTimes(Path p, long mtime, long atime) throws IOException {
		delegate.setTimes(p, mtime, atime);
	}

	public Path createSnapshot(Path path, String snapshotName) throws IOException {
		return delegate.createSnapshot(path, snapshotName);
	}

	public void renameSnapshot(Path path, String snapshotOldName, String snapshotNewName) throws IOException {
		delegate.renameSnapshot(path, snapshotOldName, snapshotNewName);
	}

	public void deleteSnapshot(Path path, String snapshotName) throws IOException {
		delegate.deleteSnapshot(path, snapshotName);
	}

	public void modifyAclEntries(Path path, List<AclEntry> aclSpec) throws IOException {
		delegate.modifyAclEntries(path, aclSpec);
	}

	public void removeAclEntries(Path path, List<AclEntry> aclSpec) throws IOException {
		delegate.removeAclEntries(path, aclSpec);
	}

	public void removeDefaultAcl(Path path) throws IOException {
		delegate.removeDefaultAcl(path);
	}

	public void removeAcl(Path path) throws IOException {
		delegate.removeAcl(path);
	}

	public void setAcl(Path path, List<AclEntry> aclSpec) throws IOException {
		delegate.setAcl(path, aclSpec);
	}

	public AclStatus getAclStatus(Path path) throws IOException {
		return delegate.getAclStatus(path);
	}

	public void setXAttr(Path path, String name, byte[] value) throws IOException {
		delegate.setXAttr(path, name, value);
	}

	public void setXAttr(Path path, String name, byte[] value, EnumSet<XAttrSetFlag> flag) throws IOException {
		delegate.setXAttr(path, name, value, flag);
	}

	public byte[] getXAttr(Path path, String name) throws IOException {
		return delegate.getXAttr(path, name);
	}

	public Map<String, byte[]> getXAttrs(Path path) throws IOException {
		return delegate.getXAttrs(path);
	}

	public Map<String, byte[]> getXAttrs(Path path, List<String> names) throws IOException {
		return delegate.getXAttrs(path, names);
	}

	public List<String> listXAttrs(Path path) throws IOException {
		return delegate.listXAttrs(path);
	}

	public void removeXAttr(Path path, String name) throws IOException {
		delegate.removeXAttr(path, name);
	}

	public void satisfyStoragePolicy(Path path) throws IOException {
		delegate.satisfyStoragePolicy(path);
	}

	public void setStoragePolicy(Path src, String policyName) throws IOException {
		delegate.setStoragePolicy(src, policyName);
	}

	public void unsetStoragePolicy(Path src) throws IOException {
		delegate.unsetStoragePolicy(src);
	}

	public BlockStoragePolicySpi getStoragePolicy(Path src) throws IOException {
		return delegate.getStoragePolicy(src);
	}

	public Collection<? extends BlockStoragePolicySpi> getAllStoragePolicies() throws IOException {
		return delegate.getAllStoragePolicies();
	}

	public Path getTrashRoot(Path path) {
		return delegate.getTrashRoot(path);
	}

	public Collection<FileStatus> getTrashRoots(boolean allUsers) {
		return delegate.getTrashRoots(allUsers);
	}

	public boolean hasPathCapability(Path path, String capability) throws IOException {
		return delegate.hasPathCapability(path, capability);
	}

	public StorageStatistics getStorageStatistics() {
		return delegate.getStorageStatistics();
	}

	@SuppressWarnings("rawtypes")
	public FSDataOutputStreamBuilder createFile(Path path) {
		return delegate.createFile(path);
	}

	@SuppressWarnings("rawtypes")
	public FSDataOutputStreamBuilder appendFile(Path path) {
		return delegate.appendFile(path);
	}

	public FutureDataInputStreamBuilder openFile(Path path) throws IOException, UnsupportedOperationException {
		return delegate.openFile(path);
	}

	public FutureDataInputStreamBuilder openFile(PathHandle pathHandle)
			throws IOException, UnsupportedOperationException {
		return delegate.openFile(pathHandle);
	}

	@SuppressWarnings("rawtypes")
	public MultipartUploaderBuilder createMultipartUploader(Path basePath) throws IOException {
		return delegate.createMultipartUploader(basePath);
	}
	

}
