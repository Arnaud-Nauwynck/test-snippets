package fr.an.hadoop.fs.dirserver.spi.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.val;

public abstract class BlobStorage {

	public abstract boolean exists(String filePath);
	
	public abstract void mkdirs(String filePath);

	public abstract void deleteFile(String filePath);

	public abstract void renameFile(String filePath, String newFilePath);


	public abstract OutputStream openWrite(String filePath, boolean append);
	
	public abstract InputStream openRead(String filePath);


	
	// only [int].. 2Go supported here
	public abstract void writeFile(String filePath, byte[] data);

	public abstract void writeAppendToFile(String filePath, byte[] appendData);


	// only [int].. 2Go supported here
	public abstract byte[] readFile(String filePath);


	// Json helper
	// ------------------------------------------------------------------------
	
	private ObjectMapper jsonMapper = new ObjectMapper();
	
	public <T> void writeFileJson(String filePath, T data) {
		try(val out = new BufferedOutputStream(openWrite(filePath, false))) {
			jsonMapper.writeValue(out, data);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to write as json to file '" + filePath + "'", ex);
		}
	}

	public <T> T readFileJson(String filePath, Class<T> type) {
		try(val in = new BufferedInputStream(openRead(filePath))) {
			return (T) jsonMapper.readValue(in, type);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to read as json from file '" + filePath + "'", ex);
		}
	}

}
