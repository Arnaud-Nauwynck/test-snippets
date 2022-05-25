package fr.an.hadoop.fs.dirserver.attrtree.encoder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import fr.an.hadoop.fs.dirserver.attrtree.AttrInfo;
import fr.an.hadoop.fs.dirserver.attrtree.AttrInfoRegistry;
import fr.an.hadoop.fs.dirserver.attrtree.DirNode;
import fr.an.hadoop.fs.dirserver.attrtree.FileNode;
import fr.an.hadoop.fs.dirserver.attrtree.Node;
import fr.an.hadoop.fs.dirserver.attrtree.Node.NodeType;
import fr.an.hadoop.fs.dirserver.attrtree.NodeAttr;
import fr.an.hadoop.fs.dirserver.util.DataOutputStreamImpl;
import fr.an.hadoop.fs.dirserver.util.StringUtils;
import lombok.val;

/**
 *
 */
public class NodeTreeFileWriter {

	public final AttrInfoRegistry attrRegistry;

	private DataOutputStreamImpl out;
	
	private String currPath = "";

	private int currAttrIndexCount = 0;
	private Map<String,Integer> currAttrIndexes = new HashMap<>();
	
	// ------------------------------------------------------------------------
	
	public NodeTreeFileWriter(AttrInfoRegistry attrRegistry, OutputStream outStream, long lastLength) {
		this.attrRegistry = attrRegistry;
		this.out = new DataOutputStreamImpl(outStream, lastLength);
	}

	public void flush() throws IOException {
		out.flush();
	}
	
	
	// ------------------------------------------------------------------------

	public void writeRecursiveNode(String path, Node node) throws IOException {
		writeNode(path, node);
		
		if (node.nodeType() == NodeType.DIR) {
			Node[] childArray = ((DirNode) node)._friend_getSortedChildArray();
			if (childArray == null || childArray.length == 0) {
				return;
			}
			val pathPrefix = (!path.isEmpty())? path + "/" : "";
			for(val child: childArray) {
				val childPath = pathPrefix + child.name;
				writeRecursiveNode(childPath, child);
			}
		}
	}
	
	public void writeNode(String path, Node node) throws IOException {
		val nodeType = node.nodeType();
		out.writeByte((nodeType == NodeType.DIR)? 'd' : 'f');

		
		// encode dir by appending "/" to path (redundant?)
		if (nodeType == NodeType.DIR) {
			path += "/";
		}
		writeIncrString(path, currPath);
		this.currPath = path;
		
		
		val creationTime = node.getCreationTime();
		out.writeLong(creationTime);

		val lastModifiedTime = node.getLastModifiedTime();
		out.writeLong(lastModifiedTime); // may use incremental: creationTime+?

		NodeAttr[] attrs = node._friend_getAttrs();
		if (attrs == null) {
			attrs = Node.EMPTY_ATTRS; // should not occur
		}
		out.writeByte(attrs.length); // assume length < 256 (128?)
		for(val attr: attrs) {
			writeAttrIndex(attr.attrInfo);
			@SuppressWarnings("unchecked")
			AttrDataEncoder<Object> attrDataEncoder = attr.attrInfo.attrDataEncoder;
			attrDataEncoder.writeData(out, attr);
		}
		
		switch(nodeType) {
		case DIR: {
			// write list of (incremental) child names
			Node[] childArray = ((DirNode) node)._friend_getSortedChildArray();
			if (childArray == null) {
				childArray = DirNode.EMPTY_CHILD; // should not occur
			}
			out.write(childArray.length);
			String currChild = "";
			for(val child: childArray) {
				writeIncrString(child.name, currChild);
				currChild = child.name;
			}
		} break;
		case FILE: {
			FileNode file = (FileNode) node;
			out.writeLong(file.getFileLength());
		} break;
		}
		
		out.write('\n'); // unnecessary, but easier for debug
	}

	protected void writeAttrIndex(AttrInfo<?> attrInfo) throws IOException {
		val attrIndex = getOrRegisterAttrIndex(attrInfo);
		out.write(attrIndex);
		if (attrIndex < 0) {
			out.writeUTF(attrInfo.name);
		}
	}

	/** return index, or (-index) if newly registered */
	protected int getOrRegisterAttrIndex(AttrInfo<?> attrInfo) {
		val name = attrInfo.name;
		val found = currAttrIndexes.get(name);
		if (found != null) {
			return found;
		}
		val index = ++currAttrIndexCount;
		currAttrIndexes.put(name, index);
		return -index;
	}
	
	protected void writeIncrString(String value, String prev) throws IOException {
		int commonLen = StringUtils.commonLen(value, prev);
		int removeLen = value.length() - commonLen;
		out.writeShort(removeLen);
		String addStr = value.substring(commonLen, value.length());
		out.writeUTF(addStr);
	}

}
