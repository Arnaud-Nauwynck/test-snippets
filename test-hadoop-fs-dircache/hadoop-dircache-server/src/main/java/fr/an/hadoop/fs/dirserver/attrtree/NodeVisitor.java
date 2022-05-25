package fr.an.hadoop.fs.dirserver.attrtree;

public abstract class NodeVisitor {

	public abstract void caseFile(FileNode node);
	public abstract void caseDir(DirNode node);

}
