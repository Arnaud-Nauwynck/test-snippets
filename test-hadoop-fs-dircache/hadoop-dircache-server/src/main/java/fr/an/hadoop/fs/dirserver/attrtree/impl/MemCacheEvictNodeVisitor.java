package fr.an.hadoop.fs.dirserver.attrtree.impl;

import fr.an.hadoop.fs.dirserver.attrtree.DirNode;
import fr.an.hadoop.fs.dirserver.attrtree.FileNode;
import fr.an.hadoop.fs.dirserver.attrtree.Node;
import fr.an.hadoop.fs.dirserver.attrtree.NodeVisitor;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class MemCacheEvictNodeVisitor extends NodeVisitor {

	private final int minLevelEvictDir;
	private final int minLevelEvictFile;
	private final long untilFreedMemSize;
	
	private int currLevel;
	private long currFreedMemSize;
	
	@Override
	public void caseFile(FileNode node) {
		// do nothing!	
	}
	@Override
	public void caseDir(DirNode node) {
		int childCount = node.getChildCount();
		if (childCount == 0) {
			return;
		}
		currLevel++;
		Object[] childNameOrNodeArray = node._friend_getSortedChildNameOrNodeArray();
		for(int i = 0; i < childCount; i++) {
			val childNameOrNode = childNameOrNodeArray[i];
			if (childNameOrNode instanceof Node) {
				Node childNode = (Node) childNameOrNode;
				if (childNode instanceof DirNode) {
					DirNode childDir = (DirNode) childNode;
					
					// *** recurse ***
					childDir.accept(this);
					
					if (currLevel > minLevelEvictDir && currFreedMemSize < untilFreedMemSize) {
						// cache evict child dir
						childNameOrNodeArray[i] = null;
						int estimateNodeMem = 120 + 50 * childNode._friend_getAttrs().length + 16 * childDir.getChildCount();
						currFreedMemSize += estimateNodeMem;
						if (currFreedMemSize > untilFreedMemSize) {
							currLevel--;
							return;
						}
					}
				} else { // if (childNode instanceof FileNode) {
					if (currLevel > minLevelEvictFile && currFreedMemSize < untilFreedMemSize) {
						// cache evict child file
						childNameOrNodeArray[i] = null;
						int estimateNodeMem = 120 + 50 * childNode._friend_getAttrs().length;
						currFreedMemSize += estimateNodeMem;
						if (currFreedMemSize > untilFreedMemSize) {
							currLevel--;
							return;
						}
					}
				}
			} // else instanceof String.. nothing to do
		}
		currLevel--;

	}
	
}