package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.CapacitySchedulerQueueInfo
 *
 */
// @XmlSeeAlso({CapacitySchedulerLeafQueueInfo.class})
@Data
public class CapacitySchedulerQueueInfo {

	  protected String queuePath;
	  protected float capacity;
	  protected float usedCapacity;
	  protected float maxCapacity;
	  protected float absoluteCapacity;
	  protected float absoluteMaxCapacity;
	  protected float absoluteUsedCapacity;
	  protected float weight;
	  protected float normalizedWeight;
	  protected int numApplications;
	  protected int maxParallelApps;
	  protected String queueName;
	  protected boolean isAbsoluteResource;
	  protected QueueState state;
	  protected CapacitySchedulerQueueInfoList queues;
	  protected ResourceInfo resourcesUsed;
	  private boolean hideReservationQueues = false;
	  protected ArrayList<String> nodeLabels = new ArrayList<String>();
	  protected long allocatedContainers;
	  protected long reservedContainers;
	  protected long pendingContainers;
	  protected QueueCapacitiesInfo capacities;
	  protected ResourcesInfo resources;
	  protected ResourceInfo minEffectiveCapacity;
	  protected ResourceInfo maxEffectiveCapacity;
	  protected ResourceInfo maximumAllocation;
	  protected QueueAclsInfo queueAcls;
	  protected int queuePriority;
	  protected String orderingPolicyInfo;
	  protected boolean autoCreateChildQueueEnabled;
	  protected LeafQueueTemplateInfo leafQueueTemplate;
	  protected String mode;
	  protected String queueType;
	  protected String creationMethod;
	  protected String autoCreationEligibility;
	  protected String defaultNodeLabelExpression;
	  protected AutoQueueTemplatePropertiesInfo autoQueueTemplateProperties =
	      new AutoQueueTemplatePropertiesInfo();
	  protected AutoQueueTemplatePropertiesInfo autoQueueParentTemplateProperties =
	      new AutoQueueTemplatePropertiesInfo();
	  protected AutoQueueTemplatePropertiesInfo autoQueueLeafTemplateProperties =
	      new AutoQueueTemplatePropertiesInfo();
}
