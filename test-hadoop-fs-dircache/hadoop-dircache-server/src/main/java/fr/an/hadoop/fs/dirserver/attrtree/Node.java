package fr.an.hadoop.fs.dirserver.attrtree;

import java.util.Map;
import java.util.TreeMap;

import lombok.Getter;
import lombok.val;

public abstract class Node {

	public enum NodeType {
		FILE,
		DIR,
	};
	
	public static final NodeAttr[] EMPTY_ATTRS = new NodeAttr[0];

	public final String name;

//	private int fileId;
//	private int fileOffset;
//	
//	private int lruCount;
//	private int lruAmortizedCount;
//
//	private long lastQueryTimestamp;
//	private long lastModifTimestamp;
	
	@Getter
	protected long creationTime;
	@Getter
    protected long lastModifiedTime;

	
	private NodeAttr[] sortedAttrArray;


	// ------------------------------------------------------------------------
	
	public Node(String name) {
		this.name = name;
	}

	public Node(String name, long creationTime, long lastModifiedTime, NodeAttr[] sortedAttrArray) {
		this.name = name;
		this.creationTime = creationTime;
		this.lastModifiedTime = lastModifiedTime;
		this.sortedAttrArray = sortedAttrArray;
	}
	
	// ------------------------------------------------------------------------

	public abstract NodeType nodeType();

	public abstract void accept(NodeVisitor visitor);
	
	public void setNodeAttrMap(TreeMap<String,NodeAttr> attrMap) {
		this.sortedAttrArray = attrMap.values().toArray(new NodeAttr[attrMap.size()]);
	}
	
	public void setBasicFileAttrs(long creationTime, long lastModifiedTime) {
		this.creationTime = creationTime;
		this.lastModifiedTime = lastModifiedTime;
	}

	public TreeMap<String,NodeAttr> getNodeAttrMapCopy() {
		val res = new TreeMap<String,NodeAttr>();
		for(val attr: sortedAttrArray) {
			res.put(attr.getName(), attr);
		}
		return res;
	}
	
	// may use ImmutableList copy?
	public NodeAttr[] _friend_getAttrs() {
		return sortedAttrArray;
	}
	
	// ------------------------------------------------------------------------
	
//	public static Builder builder(String name) {
//		return new Builder(name);
//	}
//	
//	
	public static class Builder {
		
		public final String name;

		protected long creationTime;
	    protected long lastModifiedTime;

		private Map<String,NodeAttr> attrMap;

		public Builder(String name) {
			this.name = name;
		}

		public Builder withAttr(NodeAttr attr) {
			if (attrMap == null) {
				attrMap = new TreeMap<>();
			}
			attrMap.put(attr.getName(), attr);
			return this;
		}

		protected NodeAttr[] sortedAttrArray() {
			return (attrMap == null)? EMPTY_ATTRS : attrMap.values().toArray(new NodeAttr[attrMap.size()]);
		}

		
//		public Node build() {
//			Node res = new Node(name);
//			if (childMap != null) {
//				res.sortedChildArray = childMap.values().toArray(new Node[childMap.size()]);
//			}
//		res.sortedAttrArray = sortedAttrArray();
//			return res;
//		}
	}
	
}
