package fr.an.metastore.api.dto;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class PartitionSpecDTO extends TreeMap<String,String> {

	private static final long serialVersionUID = 1L;

	public PartitionSpecDTO() {
	}

	public PartitionSpecDTO(Map<String,String> data) {
		super(data != null? data : Collections.emptyMap());
	}
	
	public String mkString(String sep) {
		return toString(); // TODO
	}

	
}