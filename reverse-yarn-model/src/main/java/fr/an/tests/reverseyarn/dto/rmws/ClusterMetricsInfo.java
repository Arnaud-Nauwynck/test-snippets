package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ClusterMetricsInfo
 */
@Data
public class ClusterMetricsInfo {

	private int appsSubmitted;
	private int appsCompleted;
	private int appsPending;
	private int appsRunning;
	private int appsFailed;
	private int appsKilled;

	private long reservedMB;
	private long availableMB;
	private long allocatedMB;
	private long pendingMB;

	private long reservedVirtualCores;
	private long availableVirtualCores;
	private long allocatedVirtualCores;
	private long pendingVirtualCores;

	private int containersAllocated;
	private int containersReserved;
	private int containersPending;

	private long totalMB;
	private long totalVirtualCores;
	private int utilizedMBPercent;
	private int utilizedVirtualCoresPercent;
	private int rmSchedulerBusyPercent;
	private int totalNodes;
	private int lostNodes;
	private int unhealthyNodes;
	private int decommissioningNodes;
	private int decommissionedNodes;
	private int rebootedNodes;
	private int activeNodes;
	private int shutdownNodes;

	private int containerAssignedPerSecond;

	// Total used resource of the cluster, including all partitions
	private ResourceInfo totalUsedResourcesAcrossPartition;

	// Total registered resources of the cluster, including all partitions
	private ResourceInfo totalClusterResourcesAcrossPartition;

	// Total reserved resources of the cluster, including all partitions.
	private ResourceInfo totalReservedResourcesAcrossPartition;

	// Total allocated containers across all partitions.
	private int totalAllocatedContainersAcrossPartition;

	private boolean crossPartitionMetricsAvailable = false;

	private int rmEventQueueSize;
	private int schedulerEventQueueSize;

}
