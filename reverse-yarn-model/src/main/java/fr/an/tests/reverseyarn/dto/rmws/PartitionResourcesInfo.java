package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.PartitionResourcesInfo
 *
 */
@Data
public class PartitionResourcesInfo {

	private String partitionName;
	private ResourceInfo used = new ResourceInfo();
	private ResourceInfo reserved;
	private ResourceInfo pending;
	private ResourceInfo amUsed;
	private ResourceInfo amLimit = new ResourceInfo();
	private ResourceInfo userAmLimit;

}
