package fr.an.tests.reverseyarn.dto;

import lombok.Value;

@Value
public class ContainerId implements Comparable<ContainerId> {

	public ApplicationAttemptId applicationAttemptId;
	public long containerId;

	@Override
	public int compareTo(ContainerId other) {
		if (this.getApplicationAttemptId().compareTo(other.getApplicationAttemptId()) == 0) {
			return Long.valueOf(getContainerId()).compareTo(Long.valueOf(other.getContainerId()));
		} else {
			return this.getApplicationAttemptId().compareTo(other.getApplicationAttemptId());
		}
	}

}
