package fr.an.fssync.fs.nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class NioMD5Utils {

    public static String md5(Path path) throws IOException {
	ByteBuffer buff = ByteBuffer.allocate(4096);
	MessageDigest md = safeMD5Digest();
	return md5(path, md, buff);
    }

    public static String md5(Path path, MessageDigest md, ByteBuffer buff) throws IOException {
	try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
	    while (channel.read(buff) != -1) {
		buff.flip();
		md.update(buff);
		buff.clear();
	    }
	    byte[] hashValue = md.digest();
	    return Hex.encodeHexString(hashValue);
	}
    }

    public static MessageDigest safeMD5Digest() {
	return getMessageDigest("MD5");
    }

    public static MessageDigest getMessageDigest(String algo) {
	MessageDigest md;
	try {
	    md = MessageDigest.getInstance(algo);
	} catch (NoSuchAlgorithmException e) {
	    throw new RuntimeException("Failed", e);
	}
	return md;
    }

    public static final byte[] fileHashUsingMappedFile(final Path file, final String algo) throws IOException {
	MessageDigest md = getMessageDigest(algo);
	return fileHashUsingMappedFile(file, md);
    }

    public static final byte[] fileHashUsingMappedFile(final Path file, final MessageDigest md) throws IOException {
	final int BUFFER = 32 * 1024;
	try (final FileChannel fc = FileChannel.open(file)) {
	    final long size = fc.size();
	    long position = 0;
	    while (position < size) {
		final MappedByteBuffer data = fc.map(FileChannel.MapMode.READ_ONLY, 0, Math.min(size, BUFFER));
		if (!data.isLoaded())
		    data.load();
		System.out.println("POS:" + position);
		md.update(data);
		position += data.limit();

		if (position >= size)
		    break;
	    }
	    return md.digest();
	}
    }

    public static final byte[] getCachedFileHash(final Path file, final String hashAlgo)
	    throws NoSuchAlgorithmException, FileNotFoundException, IOException {
	if (!Files.isReadable(file))
	    return null;
	final UserDefinedFileAttributeView view = Files.getFileAttributeView(file, UserDefinedFileAttributeView.class);
	final String name = "user.hash." + hashAlgo;
	final ByteBuffer bb = ByteBuffer.allocate(64);
	try {
	    view.read(name, bb);
	    return ((ByteBuffer) bb.flip()).array();
	} catch (final NoSuchFileException t) { // Not yet calculated
	} catch (final Throwable t) {
	    t.printStackTrace();
	}
	// System.out.println("Hash not found calculation");
	final byte[] hash = fileHashUsingMappedFile(file, hashAlgo);
	view.write(name, ByteBuffer.wrap(hash));
	return hash;
    }

}
