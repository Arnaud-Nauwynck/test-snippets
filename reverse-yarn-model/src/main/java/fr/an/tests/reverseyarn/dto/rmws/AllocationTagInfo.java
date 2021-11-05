package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AllocationTagInfo
 */
@Data
public class AllocationTagInfo {

	private String allocationTag;
	private long allocationsCount;

}
