package fr.an.tests.reverseyarn.dto.app;

import java.util.Set;

import lombok.Data;

@Data
public class ApplicationReport {

	public ApplicationId applicationId;
	
	public ApplicationAttemptId currentApplicationAttemptId;
	public String user;
	public String queue;
	public String name;
	public int rpcPort;
	public Token clientToAMToken;
	public YarnApplicationState yarnApplicationState;
	public String diagnostics;
	public String trackingUrl;
	public String originalTrackingUrl;
	public long startTime;
	public long finishTime;
	public FinalApplicationStatus finalApplicationStatus;
	public ApplicationResourceUsageReport applicationResourceUsageReport;
	public float progress;
	public String applicationType;
	public Set<String> tags;
	public Token amRMToken;

}