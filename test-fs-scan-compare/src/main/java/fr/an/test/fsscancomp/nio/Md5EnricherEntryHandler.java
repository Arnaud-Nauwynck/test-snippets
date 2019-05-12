package fr.an.test.fsscancomp.nio;

import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.MessageDigest;

import fr.an.test.fsscancomp.img.ImageEntry;
import fr.an.test.fsscancomp.img.ImageEntryHandler;

public class Md5EnricherEntryHandler extends ImageEntryHandler {

	private final ImageEntryHandler delegate;

	private long md5NanoTotal;
	private int errorCount;
	
	ByteBuffer buff = ByteBuffer.allocate(16*4096);
	MessageDigest md = NioMD5Utils.safeMD5Digest();
	
	public Md5EnricherEntryHandler(ImageEntryHandler delegate) {
		this.delegate = delegate;
	}

	
	public long getMd5NanoTotal() {
		return md5NanoTotal;
	}

	public int getErrorCount() {
		return errorCount;
	}

	@Override
	public void handle(ImageEntry entry) {
		if (entry.isFile) {
			long startNano = System.nanoTime();
			Path path = FileSystems.getDefault().getPath(entry.path);
			try {
				entry.md5 = NioMD5Utils.md5(path, md, buff);
				md.reset();
			} catch(Exception ex) {
				// ignore, no rethrow!
				errorCount++;
			}
			long nano = System.nanoTime() - startNano;
			md5NanoTotal += nano;
		}
		delegate.handle(entry);
	}

	@Override
	public void close() {
		delegate.close();
	}

	

}
