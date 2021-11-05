package fr.an.tests.reverseyarn.dto.rmws;

import java.util.List;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppActivitiesInfo
 */
@Data
public class AppActivitiesInfo {

	private String applicationId;
	private String diagnostic;
	private Long timestamp;
	private String dateTime;
	private List<AppAllocationInfo> allocations;

}
