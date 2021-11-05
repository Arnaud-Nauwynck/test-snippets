package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ResourceUtilizationInfo
 */
@Data
public class ResourceUtilizationInfo {

	protected int nodePhysicalMemoryMB;
	protected int nodeVirtualMemoryMB;
	protected double nodeCPUUsage;
	protected int aggregatedContainersPhysicalMemoryMB;
	protected int aggregatedContainersVirtualMemoryMB;
	protected double containersCPUUsage;

}
