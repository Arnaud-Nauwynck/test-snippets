package fr.an.hadoop.fsimagetool.io.codec;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import fr.an.hadoop.fsimagetool.io.ImageEntry;

public class ImageEntryFragmentIncrDataReader implements Closeable {

	private DataInputStream in;
	private StringBuilder prevPath = new StringBuilder(5000);
	
	public ImageEntryFragmentIncrDataReader(InputStream in) {
		this.in = new DataInputStream(in);
	}

	@Override
	public void close() {
		try {
			in.close();
		} catch (IOException e) {
		}
	}
	
	public ImageEntry readEntry() {
		boolean isFile;
		try {
			isFile = in.readBoolean();
		} catch (EOFException ex) {
			return null;
		} catch (IOException ex) {
			throw new RuntimeException("", ex);
		}

		try {
			int removeCount = in.readShort();
			String addPath = in.readUTF();

			int commonPart = prevPath.length() - removeCount; 
			if (commonPart < prevPath.length()) {
				prevPath.delete(commonPart, prevPath.length());
			}
			prevPath.append(addPath); 
			String path = prevPath.toString();
			
//			// duplicate .. for debug
//			String checkPath = in.readUTF();
//			if (!checkPath.equals(path)) {
//				System.err.println("unexpected path differs:\n" + 
//						"expected: " + checkPath + "\n" +
//						"actual  : " + path);
//				System.err.println();
//			}
			
			long lastModified = in.readLong();
			long length = (isFile)? in.readLong() : 0;
			in.readChar();
			
			return new ImageEntry(isFile, path, lastModified, length);
		} catch (IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
	}

}
