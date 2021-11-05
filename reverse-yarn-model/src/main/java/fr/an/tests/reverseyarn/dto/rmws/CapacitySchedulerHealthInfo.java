package fr.an.tests.reverseyarn.dto.rmws;

import java.util.List;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.CapacitySchedulerHealthInfo
 */
@Data
public class CapacitySchedulerHealthInfo {

	long lastrun;
	List<OperationInformation> operationsInfo;
	List<LastRunDetails> lastRunDetails;

	@Data
	public static class OperationInformation {
		String operation;
		String nodeId;
		String containerId;
		String queue;
	}

	@Data
	public static class LastRunDetails {
		String operation;
		long count;
		ResourceInfo resources;
	}

}
