package fr.an.hadoop.fs.dirserver.spi.storage;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileBlobStorage extends BlobStorage {

	public final String displayName;

	public final File baseDir;
	
	// ------------------------------------------------------------------------
	
	public FileBlobStorage(String displayName, File baseDir) {
		this.displayName = displayName;
		this.baseDir = baseDir;
	}

	// ------------------------------------------------------------------------
	
	protected File toFile(String subFilePath) {
		return new File(baseDir, subFilePath);
	}
	
	@Override
	public boolean exists(String filePath) {
		val file = toFile(filePath);
		return file.exists();
	}
	
	@Override
	public void mkdirs(String filePath) {
		log.info("mkdirs " + displayName + " '" + filePath + "'");
		val file = toFile(filePath);
		file.mkdirs();
	}

	@Override
	public void writeFile(String filePath, byte[] data) {
		log.info("write to " + displayName + " file '" + filePath + "'");
		val file = toFile(filePath);
		try (val out = new FileOutputStream(file)) {
			out.write(data);
		} catch(IOException ex) {
			throw new RuntimeException("Failed to write to file '" + filePath + "'", ex);
		}
	}

	@Override
	public void writeAppendToFile(String filePath, byte[] appendData) {
		log.info("write append to " + displayName + " file '" + filePath + "'");
		val file = toFile(filePath);
		try (val out = new FileOutputStream(file, true)) {
			out.write(appendData);
		} catch(IOException ex) {
			throw new RuntimeException("Failed to write append to file '" + filePath + "'", ex);
		}
	}

	@Override
	public byte[] readFile(String filePath) {
		log.info("read " + displayName + " file '" + filePath + "'");
		val file = toFile(filePath);
		long lenLong = file.length(); // only 2Go supported here
		if (lenLong > Integer.MAX_VALUE) {
			throw new UnsupportedOperationException();
		}
		int len = (int) lenLong;
		byte[] res = new byte[(int) len];
		try (val in = new FileInputStream(file)) {
			// readFullly
	        int n = 0;
	        while (n < len) {
	            int count = in.read(res, n, len - n);
	            if (count < 0) {
	                throw new EOFException(); // should not occur
	            }
	            n += count;
	        }
		} catch(IOException ex) {
			throw new RuntimeException("Failed to read file '" + filePath + "'", ex);
		}
		return res;
	}

	@Override
	public void deleteFile(String filePath) {
		log.info("delete " + displayName + " file '" + filePath + "'");
		val file = toFile(filePath);
		file.delete();
	}

	@Override
	public void renameFile(String filePath, String newFilePath) {
		log.info("rename " + displayName + " file '" + filePath + "' -> '" + newFilePath + "'");
		val file = toFile(filePath);
		val newFile = toFile(newFilePath);
		file.renameTo(newFile);
	}

}
