package fr.an.hadoop.fs.dirserver.fsdata;

public abstract class FsNodeDataEntryCallback {

	public abstract void handle(String path, NodeFsData nodeData);
	
}
