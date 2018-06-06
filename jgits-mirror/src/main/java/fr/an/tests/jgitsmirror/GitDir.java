package fr.an.tests.jgitsmirror;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitDir {
	
	public final String dir;
	
	public boolean isSparse = false;
	
	public List<GitRemote> remotes = new ArrayList<>();
	
	public static class GitRemote {
		public String name;
		public String url;
		
		public GitRemote(@JsonProperty("name") String name, @JsonProperty("url") String url) {
			this.name = name;
			this.url = url;
		}
		
		
	}
	
	
	// ------------------------------------------------------------------------

	public GitDir(@JsonProperty("dir") String dir) {
		this.dir = dir;
	}
	
	// ------------------------------------------------------------------------
	
}
