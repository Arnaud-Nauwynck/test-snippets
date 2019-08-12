package fr.an.fssync.fs.nio;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Objects;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.visitor.FsEntryVisitor;

public class Md5EnricherEntryHandler extends FsEntryVisitor {

    private final FsEntryVisitor delegate;

    private final Path rootPath;
    
    private long md5NanoTotal;
    private int errorCount;

    private ByteBuffer buff = ByteBuffer.allocate(16 * 4096);
    private MessageDigest md = NioMD5Utils.safeMD5Digest();

    public Md5EnricherEntryHandler(Path rootPath, FsEntryVisitor delegate) {
	this.rootPath = rootPath;
	this.delegate = delegate;
    }

    public long getMd5NanoTotal() {
	return md5NanoTotal;
    }

    public int getErrorCount() {
	return errorCount;
    }

    @Override
    public void begin() {
	// do nothing
    }
    
    @Override
    public void visit(FsEntry entry) {
	if (entry.isFile()) {
	    long startNano = System.nanoTime();
		Path path = rootPath.resolve(entry.path.toUri()); // FileSystems.getDefault().getPath(entry.path);
		try {
		    String md5 = NioMD5Utils.md5(path, md, buff);
		    if (! Objects.equals(md5, entry.info.md5)) {
			entry.info = entry.info.builderCopy().md5(md5).build();
		    }
		md.reset();
	    } catch (Exception ex) {
		// ignore, no rethrow!
		errorCount++;
	    }
	    long nano = System.nanoTime() - startNano;
	    md5NanoTotal += nano;
	}
	delegate.visit(entry);
    }

    @Override
    public void end() {
	delegate.end();
    }

}
