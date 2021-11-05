package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ResourceOptionInfo
 */
@Data
public class ResourceOptionInfo {

	private ResourceInfo resource = new ResourceInfo();

	private int overCommitTimeout;

//	  /** Internal resource option for caching. */
//	  private ResourceOption resourceOption;

}
