package fr.an.hadoop.fs.dirserver.attrtree.cache;

import com.google.common.collect.ImmutableMap;

import fr.an.hadoop.fs.dirserver.fsdata.NodeFsData;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CachedNodeData {

	public final NodeFsData nodeFsData;
	
	public final ImmutableMap<String,CachedNodeData> nodeAttrs;
	
	
}
