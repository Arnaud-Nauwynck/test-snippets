package fr.an.hadoop.fs.dirserver.attrtree;

import java.util.TreeMap;

import fr.an.hadoop.fs.dirserver.attrtree.cache.CachedNodeData;
import fr.an.hadoop.fs.dirserver.attrtree.cache.CachedNodeDataLoader;
import fr.an.hadoop.fs.dirserver.attrtree.encoder.NodeTreeFileReader.ReadNodeEntry;
import lombok.val;

public class DirNode extends Node {
	
	private static final long[] EMPTY_CHILD_FILEPOS = new long[0];

	private static final Object[] EMPTY_CHILD = new Object[0];
	
	private static final Node[] EMPTY_CHILD_NODES = new Node[0];

	public static final String[] EMPTY_CHILD_NAMES = new String[0];

	
	private long[] sortedChildFilePosArray;

	// when loaded => downcast to Node
	// when not loaded => downcast to String
	private Object[] sortedChildNameOrNodeArray; 
	
	// ------------------------------------------------------------------------
	
	public DirNode(String name) {
		super(name);
	}

	// constructor when building from exhaustive scan (in-memory)
	public DirNode(String name, long creationTime, long lastModifiedTime, NodeAttr[] sortedAttrArray, //
			TreeMap<String,Node> sortedChildMap) {
		super(name, creationTime, lastModifiedTime, sortedAttrArray);
		if (sortedChildMap.isEmpty()) {
			sortedChildFilePosArray = EMPTY_CHILD_FILEPOS;
			sortedChildNameOrNodeArray = EMPTY_CHILD;
		} else {
			val childCount = sortedChildMap.size();
			val posArray = new long[childCount];
			val nodeArray = new Object[childCount];
			int i = 0;
			for(val child: sortedChildMap.values()) {
				posArray[i] = 0; // none!
				nodeArray[i] = child;
				i++;
			}
			this.sortedChildFilePosArray = posArray;
			this.sortedChildNameOrNodeArray = nodeArray;
		}
	}

	// TODO remove..?
	// temporary constructor when reading from file
	private DirNode(String name, long creationTime, long lastModifiedTime, NodeAttr[] sortedAttrArray, //
			String[] childNames) {
		super(name, creationTime, lastModifiedTime, sortedAttrArray);
		val childCount = childNames.length;
		if (childCount == 0) {
			sortedChildFilePosArray = EMPTY_CHILD_FILEPOS;
			sortedChildNameOrNodeArray = EMPTY_CHILD;
		} else {
			val nodeArray = new Object[childCount];
			for (int i = 0; i < childCount; i++) {
				nodeArray[i] = childNames[i];
			}
			this.sortedChildFilePosArray = null; // not available yet
			this.sortedChildNameOrNodeArray = nodeArray;
		}
	}

	public static DirNode _friend_beginCreateFromFile(String name, long creationTime, long lastModifiedTime, NodeAttr[] sortedAttrArray, //
			String[] childNames) {
		return new DirNode(name, creationTime, lastModifiedTime, sortedAttrArray, childNames);
	}
	
	// finish... constructor when reading from file
	public void _friend_finishCreateFromFile(ReadNodeEntry[] childEntries) {
		val childCount = childEntries.length;
		if (childCount == 0) {
			sortedChildFilePosArray = EMPTY_CHILD_FILEPOS;
			sortedChildNameOrNodeArray = EMPTY_CHILD;
		} else {
			val posArray = new long[childCount];
			val nodeArray = new Object[childCount];
			val checkChildNames = sortedChildNameOrNodeArray;
			if (checkChildNames.length != childCount) {
				throw new IllegalStateException();
			}
			for(int i = 0; i < childCount; i++) {
				posArray[i] = childEntries[i].filePos;
				val childNode = childEntries[i].node;
				if (! checkChildNames[i].equals(childNode.name)) {
					throw new IllegalStateException();
				}
				nodeArray[i] = childNode;
			}
			this.sortedChildFilePosArray = posArray;
			this.sortedChildNameOrNodeArray = nodeArray;
		}
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

	public int getChildCount() {
		return sortedChildFilePosArray.length;
	}

	public String[] getSortedChildNames() {
		int childCount = sortedChildFilePosArray.length;
		if (childCount == 0) {
			return EMPTY_CHILD_NAMES;
		}
		val res = new String[childCount]; 
		for(int i = 0; i < childCount; i++) {
			Object nameOrNode = sortedChildNameOrNodeArray[i];
			res[i] = (nameOrNode instanceof String)? (String) nameOrNode : ((Node) nameOrNode).name;
		}
		return res;
	}

	public Node[] getSortedChildNodes() {
		int childCount = sortedChildFilePosArray.length;
		if (childCount == 0) {
			return EMPTY_CHILD_NODES;
		}
		val res = new Node[childCount]; 
		for(int i = 0; i < childCount; i++) {
			Object nameOrNode = sortedChildNameOrNodeArray[i];
			Node node;
			if (nameOrNode instanceof Node) {
				node = (Node) nameOrNode;
			} else {
				node = null; // TODO not implemented yet !!! => need read file 
			}
			res[i] = node;
		}
		return res;
	}

	
	public Object[] _friend_getSortedChildNameOrNodeArray() {
		return sortedChildNameOrNodeArray;
	}

	public Node _friend_getOrResolveChildNode(String[] pathElts, int pathIdx,
			CachedNodeDataLoader cachedNodeDataLoader) {
		String childName = pathElts[pathIdx];
		// dichotomy search child index by name
		val childArray = sortedChildNameOrNodeArray; 
		val childFilePosArray = sortedChildFilePosArray;
        int low = 0;
        int high = childArray.length - 1;
        int foundIdx = -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            Object midVal = childArray[mid];
            String midName = (midVal instanceof String)? (String) midVal : ((Node) midVal).name;
            int cmp = midName.compareTo(childName);
            
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
            	foundIdx = mid;
            	break;
            }
        }
        if (foundIdx == -1) {
        	return null;
        }
        Object foundObj = childArray[foundIdx];
        if (foundObj instanceof Node) {
        	return (Node) foundObj;
        }
        // else need to re-load (recursively) from file!
        long filePos = childFilePosArray[foundIdx];
        
        // *** the biggy ***
        CachedNodeData cachedNodeData = cachedNodeDataLoader.load(filePos, pathElts, pathIdx);

        if (cachedNodeData == null) {
        	return null; // ??
        } else {
        	throw new UnsupportedOperationException("TODO NOT IMPL YET");
        }
//        Node res = null; // TODO
//        
//        childArray[foundIdx] = res;
//        return res;
	}


	@Override
	public String toString() {
		return name + "(Dir " 
				+ ((sortedChildNameOrNodeArray != null)? "childCount=" + sortedChildNameOrNodeArray.length: 0) 
				+ ")";
	}
	
	// ------------------------------------------------------------------------
	
	public static class DirNodeBuilder extends Builder {
		
		private TreeMap<String,Node> childMap = new TreeMap<>();
		
		public DirNodeBuilder(String name) {
			super(name);
		}

		public Builder withChild(Node child) {
			childMap.put(child.name, child);
			return this;
		}

		public DirNode build() {
			val res = new DirNode(name, creationTime, lastModifiedTime, sortedAttrArray(), childMap);
			return res;
		}
	}

}
