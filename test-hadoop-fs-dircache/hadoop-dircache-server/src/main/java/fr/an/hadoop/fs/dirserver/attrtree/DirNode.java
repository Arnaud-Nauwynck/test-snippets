package fr.an.hadoop.fs.dirserver.attrtree;

import java.util.Map;
import java.util.TreeMap;

import fr.an.hadoop.fs.dirserver.attrtree.Node.NodeType;
import lombok.val;

public class DirNode extends Node {
	
	public static final Node[] EMPTY_CHILD = new Node[0];
	
	private Node[] sortedChildArray;

	// ------------------------------------------------------------------------
	
	public DirNode(String name) {
		super(name);
	}

	public DirNode(String name, long creationTime, long lastModifiedTime, NodeAttr[] sortedAttrArray, Node[] sortedChildArray) {
		super(name, creationTime, lastModifiedTime, sortedAttrArray);
		this.sortedChildArray = sortedChildArray;
	}

	
	// ------------------------------------------------------------------------

	@Override
	public NodeType nodeType() {
		return NodeType.DIR;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.caseDir(this);
	}


	public void setChildMap(TreeMap<String,Node> childMap) {
		this.sortedChildArray = childMap.values().toArray(new Node[childMap.size()]);
	}

	public TreeMap<String,Node> getChildMapCopy() {
		val res = new TreeMap<String,Node>();
		for(val node: sortedChildArray) {
			res.put(node.name, node);
		}
		return res;
	}
	
	// my use ImmutableList ..
	public Node[] _friend_getSortedChildArray() {
		return sortedChildArray;
	}

	@Override
	public String toString() {
		return name + "(Dir " 
				+ ((sortedChildArray != null)? "childCount=" + sortedChildArray.length: 0) 
				+ ")";
	}
	
	// ------------------------------------------------------------------------
	
	public static class DirNodeBuilder extends Builder {
		
		private Map<String,Node> childMap = new TreeMap<>();
		
		public DirNodeBuilder(String name) {
			super(name);
		}

		public Builder withChild(Node child) {
			childMap.put(child.name, child);
			return this;
		}

		public DirNode build() {
			Node[] sortedChildArray = (childMap.isEmpty())? EMPTY_CHILD : childMap.values().toArray(new Node[childMap.size()]);
			val res = new DirNode(name, creationTime, lastModifiedTime, sortedAttrArray(), sortedChildArray);
			return res;
		}
	}
	
}
