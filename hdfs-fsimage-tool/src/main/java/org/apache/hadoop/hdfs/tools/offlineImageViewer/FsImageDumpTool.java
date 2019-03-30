package org.apache.hadoop.hdfs.tools.offlineImageViewer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.hadoop.hdfs.server.namenode.FsImageProto.INodeSection.INode;

public class FsImageDumpTool {

	public static abstract class EntryCallback {
		public abstract void onEntry(String parent, INode inode);
	}
	
	public static void processFsImage(File fsImage, EntryCallback callback) throws IOException {
		String tempPath = "";
		
		try (PBImageEntryVisitor pbImgVisitor = new PBImageEntryVisitor(tempPath, callback)) {
			pbImgVisitor.visit(new RandomAccessFile(fsImage, "r"));
        }
	}
	
	static class PBImageEntryVisitor extends PBImageTextWriter {
		EntryCallback callback;
		
		public PBImageEntryVisitor(String tempPath, EntryCallback callback) throws IOException {
			super(new PrintStream(new NullOutputStream()), tempPath);
			this.callback = callback;
		}

		@Override
		protected String getEntry(String parent, INode inode) {
			return "";
		}

		@Override
		protected String getHeader() {
			return "";
		}
		
	}
}
