package fr.an.tests.reverseyarn.dto;

import lombok.Value;

@Value
public class ApplicationAttemptId implements Comparable<ApplicationAttemptId> {
	public static final String appAttemptIdStrPrefix = "appattempt_";

	public final ApplicationId applicationId;
	public final int attemptId;

	@Override
	public int compareTo(ApplicationAttemptId other) {
		int compareAppIds = this.getApplicationId().compareTo(other.getApplicationId());
		if (compareAppIds == 0) {
			return this.getAttemptId() - other.getAttemptId();
		} else {
			return compareAppIds;
		}
	}
}
