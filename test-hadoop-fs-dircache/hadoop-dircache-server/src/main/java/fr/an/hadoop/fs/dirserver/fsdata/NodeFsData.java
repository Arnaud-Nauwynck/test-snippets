package fr.an.hadoop.fs.dirserver.fsdata;

import java.util.TreeMap;

import com.google.common.collect.ImmutableMap;

import lombok.Getter;

@Getter
public abstract class NodeFsData {
	public final String name;

	public final long creationTime;

	public final long lastModifiedTime;

	public final ImmutableMap<String,Object> extraFsAttrs;
	
	// ------------------------------------------------------------------------
	
	public NodeFsData(String name, long creationTime, long lastModifiedTime, ImmutableMap<String,Object> extraFsAttrs) {
		this.name = name;
		this.creationTime = creationTime;
		this.lastModifiedTime = lastModifiedTime;
		this.extraFsAttrs = extraFsAttrs;
	}

	// ------------------------------------------------------------------------

	public abstract void accept(NodeFsDataVisitor visitor);
	
	public static abstract class NodeFsDataVisitor {
		public abstract void caseFile(FileNodeFsData node);
		public abstract void caseDir(DirNodeFsData node);
	}
	
	// ------------------------------------------------------------------------
	
	@Getter
	public static class FileNodeFsData extends NodeFsData {

		public final long fileLength;

		public FileNodeFsData(String name, long creationTime, long lastModifiedTime, ImmutableMap<String,Object> extraFsAttrs, //
				long fileLength) {
			super(name, creationTime, lastModifiedTime, extraFsAttrs);
			this.fileLength = fileLength;
		}

		@Override
		public void accept(NodeFsDataVisitor visitor) {
			visitor.caseFile(this);
		}

	}

	// ------------------------------------------------------------------------
	
	@Getter
	public static class DirNodeFsData extends NodeFsData {
		
		// (may be immutable) ensured sorted + unique per name 
		public final TreeMap<String,DirEntryNameAndType> childEntries;

		public DirNodeFsData(String name, long creationTime, long lastModifiedTime, ImmutableMap<String,Object> extraFsAttrs, //
				TreeMap<String,DirEntryNameAndType> childEntries) {
			super(name, creationTime, lastModifiedTime, extraFsAttrs);
			this.childEntries = childEntries;
		}
		
		@Override
		public void accept(NodeFsDataVisitor visitor) {
			visitor.caseDir(this);
		}
		
	}

}
