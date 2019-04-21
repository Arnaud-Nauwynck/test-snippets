package fr.an.hadoop.fsimagetool.io.codec;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import fr.an.hadoop.fsimagetool.io.ImageEntry;
import fr.an.hadoop.fsimagetool.io.ImageEntryHandler;

public class ImageEntryFragmentDataWriter extends ImageEntryHandler {

	private DataOutputStream out;
	private StringBuilder prevPath = new StringBuilder(5000);
	
	public ImageEntryFragmentDataWriter(OutputStream out) {
		this.out = new DataOutputStream(out);
	}

	@Override
	public void handle(ImageEntry e) {
		try {
			boolean isFile = e.isFile;
			out.writeBoolean(isFile);
			String path = e.path;
			
			// find common part between path and prevPath
			int commonPart = commonPart(path, prevPath);
			int removeCount = prevPath.length() - commonPart;
			String addPath = (commonPart < path.length())? 
					path.substring(commonPart, path.length()) 
					: ""; // should not occur

			out.writeShort(removeCount);
			out.writeUTF(addPath);

			if (commonPart < prevPath.length()) {
				prevPath.delete(commonPart, prevPath.length());
			}
			prevPath.append(addPath);

//			// check ..
//			if (! prevPath.toString().equals(path)) {
//				System.err.println("different path\n" +
//						"expected : " + path + "\n" +
//						"actual   : " + prevPath.toString());
//				System.err.println();
//			}
//			
//			// duplicate .. for debug
//			out.writeUTF(path);


			out.writeLong(e.lastModified);
			if (isFile) {
				out.writeLong(e.length);
			}
			out.writeChar('\n');
		} catch (IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
	}

	private static int commonPart(String left, StringBuilder right) {
		int maxLen = Math.min(left.length(), right.length());
		for(int i = 0; i < maxLen; i++) {
			if (left.charAt(i) != right.charAt(i)) {
				return i-1;
			}
		}
		return maxLen;
	}
	
	@Override
	public void close() {
		try {
			out.close();
		} catch(Exception ex) {
			throw new RuntimeException("", ex);
		}
	}

}
