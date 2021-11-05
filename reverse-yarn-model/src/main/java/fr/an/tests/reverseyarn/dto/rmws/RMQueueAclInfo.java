package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.RMQueueAclInfo
 */
@Data
public class RMQueueAclInfo {

	  protected boolean allowed;
	  protected String user;
	  protected String diagnostics;

}
