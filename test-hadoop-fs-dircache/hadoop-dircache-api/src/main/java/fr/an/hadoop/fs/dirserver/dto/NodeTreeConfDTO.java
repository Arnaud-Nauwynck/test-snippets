package fr.an.hadoop.fs.dirserver.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor
public class NodeTreeConfDTO {

	@NoArgsConstructor @AllArgsConstructor
	public static class NodeTreeMountDirConfDTO {
		public String mountName;
		public String baseUrl;
	}
	
	public List<NodeTreeMountDirConfDTO> mounts;
	
	public List<String> indexedAttrNames;
	
	
}
