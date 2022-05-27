package fr.an.hadoop.fs.dirserver.spi.storage;

public abstract class BlobStorage {

	public abstract boolean exists(String filePath);
	
	public abstract void mkdirs(String filePath);

	// only [int].. 2Go supported here
	public abstract void writeFile(String filePath, byte[] data);

	public abstract void writeAppendToFile(String filePath, byte[] appendData);

	// only [int].. 2Go supported here
	public abstract byte[] readFile(String filePath);

	public abstract void deleteFile(String filePath);

	public abstract void renameFile(String filePath, String newFilePath);

}
