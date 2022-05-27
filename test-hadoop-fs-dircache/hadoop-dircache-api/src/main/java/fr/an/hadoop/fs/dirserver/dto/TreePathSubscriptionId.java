package fr.an.hadoop.fs.dirserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class TreePathSubscriptionId {

	private final long opaqueId;

	public TreePathSubscriptionId(@JsonProperty("opaqueId") long opaqueId) {
		this.opaqueId = opaqueId;
	}
	
}
