package fr.an.hadoop.fs.dirserver.attrtree.scan;

import fr.an.hadoop.fs.dirserver.attrtree.DirNode;
import fr.an.hadoop.fs.dirserver.attrtree.FileNode;
import fr.an.hadoop.fs.dirserver.attrtree.NodeVisitor;
import lombok.Getter;
import lombok.val;

@Getter
public class CountNodeVisitor extends NodeVisitor {

	protected int countFile;
	protected int countDir;
	
	@Override
	public void caseFile(FileNode node) {
		countFile++;
	}

	@Override
	public void caseDir(DirNode node) {
		countDir++;
		val childLs = node._friend_getSortedChildArray();
		if (childLs != null && childLs.length != 0) {
			for(val child: childLs) {
				child.accept(this);
			}
		}
	}

	
}
