package fr.an.tests.reverseyarn.dto.rmws;

import java.util.List;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ActivityNodeInfo
 */
@Data
public class ActivityNodeInfo {

	private String name; // The name for activity node
	private Integer appPriority;
	private Integer requestPriority;
	private Long allocationRequestId;
	private String allocationState;
	private String diagnostic;
	private String nodeId;

	// Used for groups of activities
	private Integer count;
	private List<String> nodeIds;

	protected List<ActivityNodeInfo> children;

}
