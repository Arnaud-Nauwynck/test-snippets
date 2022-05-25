package fr.an.hadoop.fs.dirserver.attrtree;

import fr.an.hadoop.fs.dirserver.attrtree.Node.NodeType;
import lombok.Getter;

public class FileNode extends Node {

	@Getter
	private long fileLength;
	
	// ------------------------------------------------------------------------
	
	public FileNode(String name) {
		super(name);
	}

	public FileNode(String name, long creationTime, long lastModifiedTime, NodeAttr[] sortedAttrArray, long fileLength) {
		super(name, creationTime, lastModifiedTime, sortedAttrArray);
		this.fileLength = fileLength;
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public NodeType nodeType() {
		return NodeType.FILE;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.caseFile(this);
	}

	public void setBasicFileAttrs(long creationTime, long lastModifiedTime, long fileLength) {
		super.setBasicFileAttrs(creationTime, lastModifiedTime);
		this.fileLength = fileLength;
	}

	@Override
	public String toString() {
		return name + "(File len=" + fileLength + ")";
	}
	
}
