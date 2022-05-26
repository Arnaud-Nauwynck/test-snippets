package fr.an.hadoop.fs.dirserver.attrtree.encoder;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.io.CountingInputStream;

import fr.an.hadoop.fs.dirserver.attrtree.DirNode;
import fr.an.hadoop.fs.dirserver.attrtree.FileNode;
import fr.an.hadoop.fs.dirserver.attrtree.Node;
import fr.an.hadoop.fs.dirserver.attrtree.Node.NodeType;
import fr.an.hadoop.fs.dirserver.attrtree.NodeAttr;
import fr.an.hadoop.fs.dirserver.attrtree.attrinfo.AttrInfo;
import fr.an.hadoop.fs.dirserver.attrtree.attrinfo.AttrInfoIndexes;
import fr.an.hadoop.fs.dirserver.attrtree.attrinfo.AttrInfoRegistry;
import lombok.AllArgsConstructor;
import lombok.val;

public class NodeTreeFileReader {

	public final AttrInfoIndexes attrIndexes;

	private CountingInputStream countingInputStream;
	private DataInputStream in;
	
	private String currPath = "";

	// ------------------------------------------------------------------------
	
	public NodeTreeFileReader(AttrInfoIndexes attrIndexes, InputStream inStream) {
		this.attrIndexes = attrIndexes;
		this.countingInputStream = new CountingInputStream(inStream);
		this.in = new DataInputStream(countingInputStream);
	}

	public void close() throws IOException {
		in.close();
	}
	
	
	// ------------------------------------------------------------------------

	public ReadNodeEntry readRecursiveNode(Consumer<ReadNodeEntry> optCallback) throws IOException {
		ReadNodeEntry entry = readNode();
		if (optCallback != null) {
			optCallback.accept(entry);
		}
		
		val node = entry.node;
		if (node.nodeType() == NodeType.DIR) {
			DirNode dirNode = (DirNode) node;
			int childCount = dirNode.getChildCount();
			val childEntries = new ReadNodeEntry[childCount]; 
			for(int i = 0; i < childCount; i++) {
				childEntries[i] = readRecursiveNode(optCallback);
			}
			// update in-mem child nodes + filePos for dirNode
			dirNode._friend_finishCreateFromFile(childEntries);
		}
		return entry;
	}

	@AllArgsConstructor
	public static class ReadNodeEntry {
		public final String path;
		public final Node node;
		public final long filePos;
	}
	
	protected ReadNodeEntry readNode() throws IOException {
		long filePos = countingInputStream.getCount();
		
		val noteTypeByte = in.readByte();
		val nodeType = (noteTypeByte == 'd')? NodeType.DIR : NodeType.FILE;
		
		String path = readIncrString(currPath);
		this.currPath = path;
		// dir encoded by appending "/" to path (redundant?)
		if (nodeType == NodeType.DIR) {
			path = path.substring(0, path.length() - 1);
		}
		
		int idxSlash = path.lastIndexOf('/');
		String name = (idxSlash != -1)? path.substring(idxSlash) : path;
		
		val creationTime = in.readLong();
		val lastModifiedTime = in.readLong(); // may use incremental: creationTime+?

		int attrLen = in.readByte(); // assume length < 256 (128?)
		NodeAttr[] attrs = (attrLen != 0)? new NodeAttr[attrLen] : Node.EMPTY_ATTRS;
		for(int i = 0;  i < attrLen; i++) {
			val attrInfo = readAttrByIndex();
			@SuppressWarnings("unchecked")
			AttrDataEncoder<Object> attrDataEncoder = attrInfo.attrDataEncoder;
			val data = attrDataEncoder.readData(in);
			attrs[i] = new NodeAttr(attrInfo, data);
		}
		
		Node node;
		switch(nodeType) {
		case DIR: {
			// read list of (incremental) child names
			int childCount = in.readInt();
			String[] childNamesArray = (childCount != 0)? new String[childCount] : DirNode.EMPTY_CHILD_NAMES;
			String currChild = "";
			for(int i = 0; i < childCount; i++) {
				childNamesArray[i] = readIncrString(currChild);
				currChild = childNamesArray[i];
			}
			node = DirNode._friend_beginCreateFromFile(name, creationTime, lastModifiedTime, attrs, //
					childNamesArray);
		} break;
		case FILE: {
			val fileLength = in.readLong();
			node = new FileNode(name, creationTime, lastModifiedTime, attrs, //
					fileLength);
		} break;
		default:
			throw new IllegalStateException();
		}
		
		val chNewLine = (char) in.read();
		if (chNewLine != '\n') throw new IllegalStateException();

		return new ReadNodeEntry(path, node, filePos);
	}

	protected AttrInfo<Object> readAttrByIndex() throws IOException {
		val attrIndex = in.readByte();
		return attrIndexes.indexToAttr(attrIndex);
	}
	
	protected String readIncrString(String prev) throws IOException {
		int removeLen = in.readShort();
		val addStr = in.readUTF();
		int commonLen = prev.length() - removeLen;
		if (commonLen == 0) {
			return addStr;
		} else {
			return prev.subSequence(0, commonLen) + addStr;
		} 
	}

}
