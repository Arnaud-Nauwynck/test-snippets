package fr.an.tests.reverseyarn.dto.app;

import javax.annotation.Resource;

import lombok.Data;

@Data
public class ApplicationResourceUsageReport {

	public int numUsedContainers;
	public int numReservedContainers;
	public Resource usedResources;
	public Resource reservedResources;
	public Resource neededResources;
	public long memorySeconds;
	public long vcoreSeconds;
	
}
