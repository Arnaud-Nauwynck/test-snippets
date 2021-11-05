package fr.an.tests.reverseyarn.dto.rmws;

import java.util.Set;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ResourceRequestInfo
 */
@Data
public class ResourceRequestInfo {

	  private int priority;
	  private long allocationRequestId;
	  private String resourceName;
	  private ResourceInfo capability;
	  private int numContainers;
	  private boolean relaxLocality;
	  private String nodeLabelExpression;

	  private ExecutionTypeRequestInfo executionTypeRequest;

	  private String placementConstraint;
	  private Set<String> allocationTags;

}
