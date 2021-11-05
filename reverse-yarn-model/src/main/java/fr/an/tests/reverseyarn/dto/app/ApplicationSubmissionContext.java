package fr.an.tests.reverseyarn.dto.app;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class ApplicationSubmissionContext {

	public ApplicationId applicationId;

	public String applicationName;

	public String queue;

	public Priority priority;

	public ContainerLaunchContext AMContainerSpec;

	public boolean unmanagedAM;

	public boolean cancelTokensWhenComplete;

	public int maxAppAttempts;

	public Resource resource;

	public String applicationType;

	public boolean keepContainersAcrossApplicationAttempts;

	public Set<String> applicationTags;

	public String nodeLabelExpression;

	public List<ResourceRequest> AMContainerResourceRequests;

	public long attemptFailuresValidityInterval;
	
	public LogAggregationContext logAggregationContext;
	
	public ReservationId reservationID;
	
	public Map<ApplicationTimeoutType, Long> applicationTimeouts;
	
	public Map<String, String> applicationSchedulingPropertiesMap;

}
