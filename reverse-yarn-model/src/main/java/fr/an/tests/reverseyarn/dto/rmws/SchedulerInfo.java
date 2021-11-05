package fr.an.tests.reverseyarn.dto.rmws;

import java.util.EnumSet;

import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.SchedulerInfo
 */
@Data
// @XmlSeeAlso({ CapacitySchedulerInfo.class, FairSchedulerInfo.class, FifoSchedulerInfo.class })
public class SchedulerInfo {

	protected String schedulerName;
	protected ResourceInfo minAllocResource;
	protected ResourceInfo maxAllocResource;
	protected EnumSet<SchedulerResourceTypes> schedulingResourceTypes;
	protected int maximumClusterPriority;

	// ------------------------------------------------------------------------
	
	@Data
	@EqualsAndHashCode(callSuper=true)
	public static class CapacitySchedulerInfo extends SchedulerInfo {

		protected float capacity;
		protected float usedCapacity;
		protected float maxCapacity;
		protected float weight;
		protected float normalizedWeight;
		protected String queueName;
		private String queuePath;
		protected CapacitySchedulerQueueInfoList queues;
		protected QueueCapacitiesInfo capacities;
		protected CapacitySchedulerHealthInfo health;
		protected ResourceInfo maximumAllocation;
		protected QueueAclsInfo queueAcls;
		protected int queuePriority;
		protected String orderingPolicyInfo;
		protected String mode;
		protected String queueType;
		protected String creationMethod;
		protected String autoCreationEligibility;
		protected String defaultNodeLabelExpression;
		protected AutoQueueTemplatePropertiesInfo autoQueueTemplateProperties;
		protected AutoQueueTemplatePropertiesInfo autoQueueParentTemplateProperties;
		protected AutoQueueTemplatePropertiesInfo autoQueueLeafTemplateProperties;
	}

	// ------------------------------------------------------------------------

	/**
	 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.FairSchedulerInfo
	 */
	@Data
	@EqualsAndHashCode(callSuper=true)
	public static class FairSchedulerInfo extends SchedulerInfo {
		
		private FairSchedulerQueueInfo rootQueue;

		// @XmlTransient
		// private FairScheduler scheduler;
	}

	// ------------------------------------------------------------------------

	/**
	 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.FifoSchedulerInfo
	 */
	@Data
	@EqualsAndHashCode(callSuper=true)
	public static class FifoSchedulerInfo extends SchedulerInfo {
		  protected float capacity;
		  protected float usedCapacity;
		  protected QueueState qstate;
		  protected long minQueueMemoryCapacity;
		  protected long maxQueueMemoryCapacity;
		  protected int numNodes;
		  protected int usedNodeCapacity;
		  protected int availNodeCapacity;
		  protected int totalNodeCapacity;
		  protected int numContainers;

		  @XmlTransient
		  protected String qstateFormatted;

		  @XmlTransient
		  protected String qName;

	}
}
