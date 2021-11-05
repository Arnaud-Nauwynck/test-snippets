package fr.an.tests.reverseyarn.dto.app;

import lombok.Data;

@Data
public class QueueStatistics {

	public long numAppsSubmitted;
	public long numAppsRunning;
	public long numAppsPending;
	public long numAppsCompleted;
	public long numAppsKilled;
	public long numAppsFailed;
	public long numActiveUsers;

	public long availableMemoryMB;
	public long allocatedMemoryMB;
	public long pendingMemoryMB;
	public long reservedMemoryMB;
	
	public long availableVCores;
	public long allocatedVCores;
	public long pendingVCores;
	public long pendingContainers;
	
	public long allocatedContainers;
	public long reservedContainers;
	public long reservedVCores;

}