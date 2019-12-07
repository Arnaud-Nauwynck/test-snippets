package fr.an.tests.hadoopfs;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BufferedFSInputStream;
import org.apache.hadoop.fs.CreateFlag;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FSExceptionMessages;
import org.apache.hadoop.fs.FSInputStream;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsStatus;
import org.apache.hadoop.fs.HasFileDescriptor;
import org.apache.hadoop.fs.LocalFileSystemPathHandle;
import org.apache.hadoop.fs.Options;
import org.apache.hadoop.fs.ParentNotDirectoryException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathHandle;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;

/**
 * Implement the FileSystem API for the raw local filesystem using PURE java.*,
 * and NO native code
 */
public class JavaioFileSystem extends FileSystem {

    static final URI NAME = URI.create("file:///");

    private File baseStorageDir;

    private Path workingDirPath;

    public JavaioFileSystem() {
        baseStorageDir = new File("data").getAbsoluteFile(); // cf next initialize() !!
    }

    private Path makeAbsolute(Path f) {
        if (f.isAbsolute()) {
            return f;
        } else {
            return new Path(workingDirPath, f);
        }
    }

    /** Convert a path to a File. */
    public File pathToFile(Path path) {
        checkPath(path);
        if (!path.isAbsolute()) {
            path = new Path(getWorkingDirectory(), path);
        }
        String subPath = path.toUri().getPath();
        if (subPath.equals("/")) {
            return baseStorageDir;
        }
        subPath = subPath.substring(1);
        return new File(baseStorageDir, subPath);
    }

    @Override
    public URI getUri() {
        return NAME;
    }

    @Override
    public void initialize(URI uri, Configuration conf) throws IOException {
        super.initialize(uri, conf);
        setConf(conf);

        String baseStoragePath = uri.getPath();
        // System.out.println("using uri=" + uri);
        // System.out.println("baseStoragePath=" + baseStoragePath);
        if (baseStoragePath.startsWith("/D:")) {
            baseStoragePath = baseStoragePath.substring(1);
            // System.out.println("=> using baseStoragePath=" + baseStoragePath);
        }
        this.baseStorageDir = new File(baseStoragePath).getAbsoluteFile();
        if (! baseStorageDir.exists()) {
            throw new RuntimeException("baseStoragePath " + baseStoragePath + "does not exist");
        }
        
        this.workingDirPath = new Path("/"); // ?? 

    }

    /*******************************************************
     * For open()'s FSInputStream.
     *******************************************************/
    class LocalFSFileInputStream extends FSInputStream implements HasFileDescriptor {
        private FileInputStream fis;
        private long position;

        public LocalFSFileInputStream(Path f) throws IOException {
            fis = new FileInputStream(pathToFile(f));
        }

        @Override
        public void seek(long pos) throws IOException {
            if (pos < 0) {
                throw new EOFException(FSExceptionMessages.NEGATIVE_SEEK);
            }
            fis.getChannel().position(pos);
            this.position = pos;
        }

        @Override
        public long getPos() throws IOException {
            return this.position;
        }

        @Override
        public boolean seekToNewSource(long targetPos) throws IOException {
            return false;
        }

        /*
         * Just forward to the fis
         */
        @Override
        public int available() throws IOException {
            return fis.available();
        }

        @Override
        public void close() throws IOException {
            fis.close();
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public int read() throws IOException {
            int value = fis.read();
            if (value >= 0) {
                this.position++;
                statistics.incrementBytesRead(1);
            }
            return value;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            // parameter check
            validatePositionedReadArgs(position, b, off, len);
            int value = fis.read(b, off, len);
            if (value > 0) {
                this.position += value;
                statistics.incrementBytesRead(value);
            }
            return value;
        }

        @Override
        public int read(long position, byte[] b, int off, int len) throws IOException {
            // parameter check
            validatePositionedReadArgs(position, b, off, len);
            if (len == 0) {
                return 0;
            }

            ByteBuffer bb = ByteBuffer.wrap(b, off, len);
            int value = fis.getChannel().read(bb, position);
            if (value > 0) {
                statistics.incrementBytesRead(value);
            }
            return value;
        }

        @Override
        public long skip(long n) throws IOException {
            long value = fis.skip(n);
            if (value > 0) {
                this.position += value;
            }
            return value;
        }

        @Override
        public FileDescriptor getFileDescriptor() throws IOException {
            return fis.getFD();
        }
    }

    @Override
    public FSDataInputStream open(Path f, int bufferSize) throws IOException {
        getFileStatus(f);
        return new FSDataInputStream(new BufferedFSInputStream(new LocalFSFileInputStream(f), bufferSize));
    }

    @Override
    public FSDataInputStream open(PathHandle fd, int bufferSize) throws IOException {
        if (!(fd instanceof LocalFileSystemPathHandle)) {
            fd = new LocalFileSystemPathHandle(fd.bytes());
        }
        LocalFileSystemPathHandle id = (LocalFileSystemPathHandle) fd;
        id.verify(getFileStatus(new Path(id.getPath())));
        return new FSDataInputStream(
                new BufferedFSInputStream(new LocalFSFileInputStream(new Path(id.getPath())), bufferSize));
    }

    /*********************************************************
     * For create()'s FSOutputStream.
     *********************************************************/
    class LocalFSFileOutputStream extends OutputStream {
        private FileOutputStream fos;

        private LocalFSFileOutputStream(Path f, boolean append, FsPermission permission) throws IOException {
            File file = pathToFile(f);
            this.fos = new FileOutputStream(file, append);
            // permission NO MANAGED here
        }

        @Override
        public void close() throws IOException {
            fos.close();
        }

        @Override
        public void flush() throws IOException {
            fos.flush();
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            fos.write(b, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            fos.write(b);
        }
    }

    @Override
    public FSDataOutputStream append(Path f, int bufferSize, Progressable progress) throws IOException {
        FileStatus status = getFileStatus(f);
        if (status.isDirectory()) {
            throw new IOException("Cannot append to a diretory (=" + f + " )");
        }
        return new FSDataOutputStream(new BufferedOutputStream(createOutputStreamWithMode(f, true, null), bufferSize),
                statistics, status.getLen());
    }

    @Override
    public FSDataOutputStream create(Path f, boolean overwrite, int bufferSize, short replication, long blockSize,
            Progressable progress) throws IOException {
        return create(f, overwrite, true, bufferSize, replication, blockSize, progress, null);
    }

    private FSDataOutputStream create(Path f, boolean overwrite, boolean createParent, int bufferSize,
            short replication, long blockSize, Progressable progress, FsPermission permission) throws IOException {
        if (exists(f) && !overwrite) {
            throw new FileAlreadyExistsException("File already exists: " + f);
        }
        Path parent = f.getParent();
        if (parent != null && !mkdirs(parent)) {
            throw new IOException("Mkdirs failed to create " + parent.toString());
        }
        return new FSDataOutputStream(
                new BufferedOutputStream(createOutputStreamWithMode(f, false, permission), bufferSize), statistics);
    }

    protected OutputStream createOutputStream(Path f, boolean append) throws IOException {
        return createOutputStreamWithMode(f, append, null);
    }

    protected OutputStream createOutputStreamWithMode(Path f, boolean append, FsPermission permission)
            throws IOException {
        return new LocalFSFileOutputStream(f, append, permission);
    }

    @Override
    public FSDataOutputStream createNonRecursive(Path f, FsPermission permission, EnumSet<CreateFlag> flags,
            int bufferSize, short replication, long blockSize, Progressable progress) throws IOException {
        if (exists(f) && !flags.contains(CreateFlag.OVERWRITE)) {
            throw new FileAlreadyExistsException("File already exists: " + f);
        }
        return new FSDataOutputStream(
                new BufferedOutputStream(createOutputStreamWithMode(f, false, permission), bufferSize), statistics);
    }

    @Override
    public FSDataOutputStream create(Path f, FsPermission permission, boolean overwrite, int bufferSize,
            short replication, long blockSize, Progressable progress) throws IOException {

        FSDataOutputStream out = create(f, overwrite, true, bufferSize, replication, blockSize, progress, permission);
        return out;
    }

    @Override
    public FSDataOutputStream createNonRecursive(Path f, FsPermission permission, boolean overwrite, int bufferSize,
            short replication, long blockSize, Progressable progress) throws IOException {
        FSDataOutputStream out = create(f, overwrite, false, bufferSize, replication, blockSize, progress, permission);
        return out;
    }

    @Override
    public void concat(final Path trg, final Path[] psrcs) throws IOException {
        final int bufferSize = 4096;
        try (FSDataOutputStream out = create(trg)) {
            for (Path src : psrcs) {
                try (FSDataInputStream in = open(src)) {
                    IOUtils.copyBytes(in, out, bufferSize, false);
                }
            }
        }
    }

    @Override
    public boolean rename(Path src, Path dst) throws IOException {
        // Attempt rename using Java API.
        File srcFile = pathToFile(src);
        File dstFile = pathToFile(dst);
        if (srcFile.renameTo(dstFile)) {
            return true;
        }

        // fallback..
        FileUtils.copyFile(srcFile, dstFile);
        return true;
    }

    public final boolean handleEmptyDstDirectoryOnWindows(Path src, File srcFile, Path dst, File dstFile)
            throws IOException {

        // Enforce POSIX rename behavior that a source directory replaces an
        // existing destination if the destination is an empty directory. On most
        // platforms, this is already handled by the Java API call above. Some
        // platforms (notably Windows) do not provide this behavior, so the Java API
        // call renameTo(dstFile) fails. Delete destination and attempt rename
        // again.
        try {
            FileStatus sdst = this.getFileStatus(dst);
            String[] dstFileList = dstFile.list();
            if (dstFileList != null) {
                if (sdst.isDirectory() && dstFileList.length == 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Deleting empty destination and renaming " + src + " to " + dst);
                    }
                    if (this.delete(dst, false) && srcFile.renameTo(dstFile)) {
                        return true;
                    }
                }
            }
        } catch (FileNotFoundException ignored) {
        }
        return false;
    }

    @Override
    public boolean truncate(Path f, final long newLength) throws IOException {
        FileStatus status = getFileStatus(f);
        if (status == null) {
            throw new FileNotFoundException("File " + f + " not found");
        }
        if (status.isDirectory()) {
            throw new IOException("Cannot truncate a directory (=" + f + ")");
        }
        long oldLength = status.getLen();
        if (newLength > oldLength) {
            throw new IllegalArgumentException("Cannot truncate to a larger file size. Current size: " + oldLength
                    + ", truncate size: " + newLength + ".");
        }
        try (FileOutputStream out = new FileOutputStream(pathToFile(f), true)) {
            out.getChannel().truncate(newLength);
        }
        return true;
    }

    /**
     * Delete the given path to a file or directory.
     * 
     * @param p         the path to delete
     * @param recursive to delete sub-directories
     * @return true if the file or directory and all its contents were deleted
     * @throws IOException if p is non-empty and recursive is false
     */
    @Override
    public boolean delete(Path p, boolean recursive) throws IOException {
        File f = pathToFile(p);
        if (!f.exists()) {
            // no path, return false "nothing to delete"
            return false;
        }
        if (f.isFile()) {
            return f.delete();
        } else if (!recursive && f.isDirectory() && f.list().length != 0) {
            throw new IOException("Directory " + f.toString() + " is not empty");
        }
        FileUtils.deleteDirectory(f);
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * (<b>Note</b>: Returned list is not sorted in any given order, due to reliance
     * on Java's {@link File#list()} API.)
     */
    @Override
    public FileStatus[] listStatus(Path f) throws IOException {
        File localf = pathToFile(f);
        FileStatus[] results;

        if (!localf.exists()) {
            throw new FileNotFoundException("File " + f + " does not exist");
        }

        if (localf.isDirectory()) {
            String[] names = localf.list();
            results = new FileStatus[names.length];
            int j = 0;
            for (int i = 0; i < names.length; i++) {
                try {
                    // Assemble the path using the Path 3 arg constructor to make sure
                    // paths with colon are properly resolved on Linux
                    results[j] = getFileStatus(new Path(f, new Path(null, null, names[i])));
                    j++;
                } catch (FileNotFoundException e) {
                    // ignore the files not found since the dir list may have have
                    // changed since the names[] list was generated.
                }
            }
            if (j == names.length) {
                return results;
            }
            return Arrays.copyOf(results, j);
        }

        return new FileStatus[] { getFileStatus(f) };
    }

    protected boolean mkOneDir(File p2f) throws IOException {
        return mkOneDirWithMode(new Path(p2f.getAbsolutePath()), p2f, null);
    }

    protected boolean mkOneDirWithMode(Path p, File p2f, FsPermission permission) throws IOException {
        // permission NOT MANAGED here
        boolean b = p2f.mkdir();
        return b;
    }

    /**
     * Creates the specified directory hierarchy. Does not treat existence as an
     * error.
     */
    @Override
    public boolean mkdirs(Path f) throws IOException {
        return mkdirsWithOptionalPermission(f, null);
    }

    @Override
    public boolean mkdirs(Path f, FsPermission permission) throws IOException {
        return mkdirsWithOptionalPermission(f, permission);
    }

    private boolean mkdirsWithOptionalPermission(Path f, FsPermission permission) throws IOException {
        if (f == null) {
            throw new IllegalArgumentException("mkdirs path arg is null");
        }
        Path parent = f.getParent();
        File p2f = pathToFile(f);
        File parent2f = null;
        if (parent != null) {
            parent2f = pathToFile(parent);
            if (parent2f != null && parent2f.exists() && !parent2f.isDirectory()) {
                throw new ParentNotDirectoryException("Parent path is not a directory: " + parent);
            }
        }
        if (p2f.exists() && !p2f.isDirectory()) {
            throw new FileAlreadyExistsException(
                    "Destination exists" + " and is not a directory: " + p2f.getCanonicalPath());
        }
        return (parent == null || parent2f.exists() || mkdirs(parent))
                && (mkOneDirWithMode(f, p2f, permission) || p2f.isDirectory());
    }

    @Override
    public Path getHomeDirectory() {
        return this.makeQualified(new Path(System.getProperty("user.home")));
    }

    /**
     * Set the working directory to the given directory.
     */
    @Override
    public void setWorkingDirectory(Path newDir) {
        workingDirPath = makeAbsolute(newDir);
        checkPath(workingDirPath);
    }

    @Override
    public Path getWorkingDirectory() {
        return workingDirPath;
    }

    @Override
    protected Path getInitialWorkingDirectory() {
        return this.makeQualified(new Path(System.getProperty("user.dir")));
    }

    @Override
    public FsStatus getStatus(Path p) throws IOException {
        File partition = pathToFile(p == null ? new Path("/") : p);
        // File provides getUsableSpace() and getFreeSpace()
        // File provides no API to obtain used space, assume used = total - free
        return new FsStatus(partition.getTotalSpace(), partition.getTotalSpace() - partition.getFreeSpace(),
                partition.getFreeSpace());
    }

    // In the case of the local filesystem, we can just rename the file.
    @Override
    public void moveFromLocalFile(Path src, Path dst) throws IOException {
        rename(src, dst);
    }

    // We can write output directly to the final location
    @Override
    public Path startLocalOutput(Path fsOutputFile, Path tmpLocalFile) throws IOException {
        return fsOutputFile;
    }

    // It's in the right place - nothing to do.
    @Override
    public void completeLocalOutput(Path fsWorkingFile, Path tmpLocalFile) throws IOException {
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    public String toString() {
        return "LocalFS";
    }

    @Override
    public FileStatus getFileStatus(Path f) throws IOException {
        File file = pathToFile(f);
        if (file.exists()) {
            return new JavaioFileStatus(f, file, getDefaultBlockSize(f), this);
        } else {
            throw new FileNotFoundException("File " + f + " does not exist");
        }
    }

    /**
     *
     */
    static class JavaioFileStatus extends FileStatus {

        /** */
        private static final long serialVersionUID = 1L;

        private static long getLastAccessTime(File f) throws IOException {
            long accessTime;
            try {
                accessTime = Files.readAttributes(f.toPath(), BasicFileAttributes.class).lastAccessTime().toMillis();
            } catch (NoSuchFileException e) {
                throw new FileNotFoundException("File " + f + " does not exist");
            }
            return accessTime;
        }

        JavaioFileStatus(Path path, File f, long defaultBlockSize, FileSystem fs) throws IOException {
            super(f.length(), f.isDirectory(), 1, defaultBlockSize, 
                    f.lastModified(), getLastAccessTime(f), 
                    null, null,
                    null, 
                    path // new Path(f.getPath()).makeQualified(fs.getUri(), fs.getWorkingDirectory())
                    );
        }

    }

    /**
     * Use the command chown to set owner.
     */
    @Override
    public void setOwner(Path p, String username, String groupname) throws IOException {
        // NOT MANAGED 
    }

    /**
     * Use the command chmod to set permission.
     */
    @Override
    public void setPermission(Path p, FsPermission permission) throws IOException {
        // NOT MANAGED 
    }

    /**
     * Sets the {@link Path}'s last modified time and last access time to the given
     * valid times.
     *
     * @param mtime the modification time to set (only if no less than zero).
     * @param atime the access time to set (only if no less than zero).
     * @throws IOException if setting the times fails.
     */
    @Override
    public void setTimes(Path p, long mtime, long atime) throws IOException {
        try {
            BasicFileAttributeView view = Files.getFileAttributeView(pathToFile(p).toPath(),
                    BasicFileAttributeView.class);
            FileTime fmtime = (mtime >= 0) ? FileTime.fromMillis(mtime) : null;
            FileTime fatime = (atime >= 0) ? FileTime.fromMillis(atime) : null;
            view.setTimes(fmtime, fatime, null);
        } catch (NoSuchFileException e) {
            throw new FileNotFoundException("File " + p + " does not exist");
        }
    }

    /**
     * Hook to implement support for {@link PathHandle} operations.
     * 
     * @param stat Referent in the target FileSystem
     * @param opts Constraints that determine the validity of the {@link PathHandle}
     *             reference.
     */
    protected PathHandle createPathHandle(FileStatus stat, Options.HandleOpt... opts) {
        if (stat.isDirectory() || stat.isSymlink()) {
            throw new IllegalArgumentException("PathHandle only available for files");
        }
        String authority = stat.getPath().toUri().getAuthority();
        if (authority != null && !authority.equals("file://")) {
            throw new IllegalArgumentException("Wrong FileSystem: " + stat.getPath());
        }
        Options.HandleOpt.Data data = Options.HandleOpt.getOpt(Options.HandleOpt.Data.class, opts)
                .orElse(Options.HandleOpt.changed(false));
        Options.HandleOpt.Location loc = Options.HandleOpt.getOpt(Options.HandleOpt.Location.class, opts)
                .orElse(Options.HandleOpt.moved(false));
        if (loc.allowChange()) {
            throw new UnsupportedOperationException("Tracking file movement in " + "basic FileSystem is not supported");
        }
        final Path p = stat.getPath();
        final Optional<Long> mtime = !data.allowChange() ? Optional.of(stat.getModificationTime()) : Optional.empty();
        return new LocalFileSystemPathHandle(p.toString(), mtime);
    }

    @Override
    public boolean supportsSymlinks() {
        return false;
    }

    @Override
    public void createSymlink(Path target, Path link, boolean createParent) throws IOException {
        throw new UnsupportedOperationException("Symlinks not supported");
    }

    /**
     * Return a FileStatus representing the given path. If the path refers to a
     * symlink return a FileStatus representing the link rather than the object the
     * link refers to.
     */
    @Override
    public FileStatus getFileLinkStatus(final Path f) throws IOException {
        throw new UnsupportedOperationException("Symlinks not supported");
    }

    @Override
    public Path getLinkTarget(Path f) throws IOException {
        throw new UnsupportedOperationException("Symlinks not supported");
        // Link NOT MANAGED
//        return f;
    }
}
