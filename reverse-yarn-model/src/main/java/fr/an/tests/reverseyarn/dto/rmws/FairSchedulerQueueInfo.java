package fr.an.tests.reverseyarn.dto.rmws;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
// @XmlSeeAlso({FairSchedulerLeafQueueInfo.class})
public class FairSchedulerQueueInfo {  
  private int maxApps;
  
//  @XmlTransient
//  private float fractionMemUsed;
//  @XmlTransient
//  private float fractionMemSteadyFairShare;
//  @XmlTransient
//  private float fractionMemFairShare;
//  @XmlTransient
//  private float fractionMemMaxShare;
  
  private ResourceInfo minResources;
  private ResourceInfo maxResources;
  private ResourceInfo usedResources;
  private ResourceInfo amUsedResources;
  private ResourceInfo amMaxResources;
  private ResourceInfo demandResources;
  private ResourceInfo steadyFairResources;
  private ResourceInfo fairResources;
  private ResourceInfo clusterResources;
  private ResourceInfo reservedResources;
  private ResourceInfo maxContainerAllocation;

  private long pendingContainers;
  private long allocatedContainers;
  private long reservedContainers;

  private String queueName;
  private String schedulingPolicy;

  private boolean preemptable;

  private FairSchedulerQueueInfoList childQueues;

  @Data
  @EqualsAndHashCode(callSuper=true)
  public static class FairSchedulerLeafQueueInfo extends FairSchedulerQueueInfo {
	  private int numPendingApps;
	  private int numActiveApps;
  }
}
