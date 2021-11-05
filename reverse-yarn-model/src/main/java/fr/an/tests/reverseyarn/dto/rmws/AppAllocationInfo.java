package fr.an.tests.reverseyarn.dto.rmws;

import java.util.List;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppAllocationInfo
 */
@Data
public class AppAllocationInfo {

	  private String nodeId;
	  private Long timestamp;
	  private String dateTime;
	  private String queueName;
	  private Integer appPriority;
	  private String allocationState;
	  private String diagnostic;
	  private List<AppRequestAllocationInfo> children;

}
