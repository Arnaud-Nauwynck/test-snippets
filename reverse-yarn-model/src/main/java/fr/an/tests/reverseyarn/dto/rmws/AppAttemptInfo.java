package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppAttemptInfo
 */
@Data
public class AppAttemptInfo {

	protected int id;
	protected long startTime;
	protected long finishedTime;
	protected String containerId;
	protected String nodeHttpAddress;
	protected String nodeId;
	protected String logsLink;
	protected String blacklistedNodes;
	private String nodesBlacklistedBySystem;
	protected String appAttemptId;
	private String exportPorts;
	private RMAppAttemptState appAttemptState;

}
