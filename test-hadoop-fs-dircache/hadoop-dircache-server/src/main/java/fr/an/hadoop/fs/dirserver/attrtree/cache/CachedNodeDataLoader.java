package fr.an.hadoop.fs.dirserver.attrtree.cache;

public abstract class CachedNodeDataLoader {

	public abstract CachedNodeData load(long filePos, 
			String[] pathElts, int idx
			);
	
}
