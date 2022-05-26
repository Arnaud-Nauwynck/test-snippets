package fr.an.hadoop.fs.dirserver.attrtree;

import lombok.Value;

@Value
public class TreePathSubscriptionId {

	private final long opaqueId;

	public TreePathSubscriptionId(long opaqueId) {
		this.opaqueId = opaqueId;
	}
	
}
