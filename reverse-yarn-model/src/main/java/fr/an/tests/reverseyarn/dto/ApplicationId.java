package fr.an.tests.reverseyarn.dto;

import lombok.Value;

@Value
public class ApplicationId implements Comparable<ApplicationId> {
	public static final String appIdStrPrefix = "application_";

	public final int id;
	public final long clusterTimestamp;
	
	@Override
	public int compareTo(ApplicationId other) {
		if (this.getClusterTimestamp() - other.getClusterTimestamp() == 0) {
			return this.getId() - other.getId();
		} else {
			return this.getClusterTimestamp() > other.getClusterTimestamp() ? 1
					: this.getClusterTimestamp() < other.getClusterTimestamp() ? -1 : 0;
		}
	}
}