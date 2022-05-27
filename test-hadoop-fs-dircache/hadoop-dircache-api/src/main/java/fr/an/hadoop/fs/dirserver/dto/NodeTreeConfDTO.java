package fr.an.hadoop.fs.dirserver.dto;

import java.util.List;

public class NodeTreeConfDTO {

	public static class NodeTreeMountDirConfDTO {
		public String mountName;
		public String baseUrl;
	}
	
	public List<NodeTreeMountDirConfDTO> mounts;
	
}
