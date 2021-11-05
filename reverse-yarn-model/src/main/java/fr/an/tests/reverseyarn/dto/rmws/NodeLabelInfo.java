package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeLabelInfo
 */
@Data
public class NodeLabelInfo {

	private String name;
	private boolean exclusivity;
	private PartitionInfo partitionInfo;

}
