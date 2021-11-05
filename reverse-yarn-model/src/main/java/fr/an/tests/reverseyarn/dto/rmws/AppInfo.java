package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppInfo
 */
@Data
public class AppInfo {

//	  @XmlTransient
//	  protected String appIdNum;
//	  @XmlTransient
//	  protected boolean trackingUrlIsNotReady;
//	  @XmlTransient
//	  protected String trackingUrlPretty;
//	  @XmlTransient
//	  protected boolean amContainerLogsExist = false;
//	  @XmlTransient
//	  protected ApplicationId applicationId;
//	  @XmlTransient
//	  private String schemePrefix;

	  // these are ok for any user to see
	  protected String id;
	  protected String user;
	  private String name;
	  protected String queue;
	  private YarnApplicationState state;
	  protected FinalApplicationStatus finalStatus;
	  protected float progress;
	  protected String trackingUI;
	  protected String trackingUrl;
	  protected String diagnostics;
	  protected long clusterId;
	  protected String applicationType;
	  protected String applicationTags = "";
	  protected int priority;

	  // these are only allowed if acls allow
	  protected long startedTime;
	  private long launchTime;
	  protected long finishedTime;
	  protected long elapsedTime;
	  protected String amContainerLogs;
	  protected String amHostHttpAddress;
	  private String amRPCAddress;
	  private String masterNodeId;
	  private long allocatedMB;
	  private long allocatedVCores;
	  private long reservedMB;
	  private long reservedVCores;
	  private int runningContainers;
	  private long memorySeconds;
	  private long vcoreSeconds;
	  protected float queueUsagePercentage;
	  protected float clusterUsagePercentage;
	  protected Map<String, Long> resourceSecondsMap;

	  // preemption info fields
	  private long preemptedResourceMB;
	  private long preemptedResourceVCores;
	  private int numNonAMContainerPreempted;
	  private int numAMContainerPreempted;
	  private long preemptedMemorySeconds;
	  private long preemptedVcoreSeconds;
	  protected Map<String, Long> preemptedResourceSecondsMap;

	  // list of resource requests
	  @XmlElement(name = "resourceRequests")
	  private List<ResourceRequestInfo> resourceRequests = new ArrayList<ResourceRequestInfo>();

	  protected LogAggregationStatus logAggregationStatus;
	  protected boolean unmanagedApplication;
	  protected String appNodeLabelExpression;
	  protected String amNodeLabelExpression;

	  protected ResourcesInfo resourceInfo;
	  private AppTimeoutsInfo timeouts;

}
