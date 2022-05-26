package fr.an.hadoop.fs.dirserver.fsdata;

public abstract class NodeFsDataProvider {

	public abstract NodeFsData queryNodeFsData(String[] subpath);
	
}
