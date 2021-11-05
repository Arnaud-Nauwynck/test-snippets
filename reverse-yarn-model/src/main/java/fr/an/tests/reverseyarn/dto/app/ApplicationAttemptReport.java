package fr.an.tests.reverseyarn.dto.app;

import lombok.Data;

@Data
public class ApplicationAttemptReport {

	public YarnApplicationAttemptState yarnApplicationAttemptState;
	public int rpcPort;
	public String host;
	public String diagnostics;
	public String trackingUrl;
	public String originalTrackingUrl;
	public ApplicationAttemptId applicationAttemptId;
	public ContainerId amContainerId;

}
