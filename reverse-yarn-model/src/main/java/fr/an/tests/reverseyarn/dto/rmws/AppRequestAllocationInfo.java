package fr.an.tests.reverseyarn.dto.rmws;

import java.util.List;

import lombok.Data;


/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppRequestAllocationInfo
 */
@Data
public class AppRequestAllocationInfo {

	private Integer requestPriority;
	private Long allocationRequestId;
	private String allocationState;
	private String diagnostic;
	private List<ActivityNodeInfo> children;

}
