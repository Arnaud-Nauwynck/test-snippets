package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeAllocationInfo
 */
@Data
public class NodeAllocationInfo {

	private String partition;
	private String updatedContainerId;
	private String finalAllocationState;
	private ActivityNodeInfo root;

}
