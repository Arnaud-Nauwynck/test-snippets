package fr.an.hadoop.fs.dirserver.attrtree.encoder;

import java.io.IOException;
import java.io.OutputStream;

import fr.an.hadoop.fs.dirserver.attrtree.DirNode;
import fr.an.hadoop.fs.dirserver.attrtree.FileNode;
import fr.an.hadoop.fs.dirserver.attrtree.Node;
import fr.an.hadoop.fs.dirserver.attrtree.Node.NodeType;
import fr.an.hadoop.fs.dirserver.attrtree.NodeAttr;
import fr.an.hadoop.fs.dirserver.attrtree.attrinfo.AttrInfo;
import fr.an.hadoop.fs.dirserver.attrtree.attrinfo.AttrInfoIndexes;
import fr.an.hadoop.fs.dirserver.util.DataOutputStreamImpl;
import fr.an.hadoop.fs.dirserver.util.StringUtils;
import lombok.val;

/**
 *
 */
public class NodeTreeFileWriter {

	public final AttrInfoIndexes attrIndexes;

	private DataOutputStreamImpl out;
	
	private String currPath = "";
	
	// ------------------------------------------------------------------------
	
	public NodeTreeFileWriter(AttrInfoIndexes attrIndexes, OutputStream outStream, long lastLength) {
		this.attrIndexes = attrIndexes;
		this.out = new DataOutputStreamImpl(outStream, lastLength);
	}

	public void flush() throws IOException {
		out.flush();
	}
	
	public long getFilePos() {
		return out.size();
	}

	// ------------------------------------------------------------------------

	public void writeRecursiveNode(String path, Node node) throws IOException {
		writeNode(path, node);
		
		if (node.nodeType() == NodeType.DIR) {
			Node[] childArray = ((DirNode) node).getSortedChildNodes();
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
			AttrDataEncoder<Object> attrDataEncoder = attr.attrInfo.attrDataEncoder;
			attrDataEncoder.writeData(out, attr);
		}
		
		switch(nodeType) {
		case DIR: {
			// write list of (incremental) child names
			String[] childNames = ((DirNode) node).getSortedChildNames();
			out.writeInt(childNames.length);
			String currChild = "";
			for(val childName: childNames) {
				writeIncrString(childName, currChild);
				currChild = childName;
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
		val index = attrIndexes.attrToIndex(attrInfo);
		out.writeByte(index);
	}

	
	protected void writeIncrString(String value, String prev) throws IOException {
		int commonLen = StringUtils.commonLen(value, prev);
		int removeLen = prev.length() - commonLen;
		out.writeShort(removeLen);
		String addStr = value.substring(commonLen, value.length());
		out.writeUTF(addStr);
	}

}
