package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.PartitionQueueCapacitiesInfo
 *
 */
@Data
public class PartitionQueueCapacitiesInfo {

	private String partitionName;

	private float capacity;
	private float usedCapacity;
	private float maxCapacity = 100;
	private float absoluteCapacity;
	private float absoluteUsedCapacity;
	private float absoluteMaxCapacity = 100;
	private float maxAMLimitPercentage;
	private float weight;
	private float normalizedWeight;
	private ResourceInfo configuredMinResource;
	private ResourceInfo configuredMaxResource;
	private ResourceInfo effectiveMinResource;
	private ResourceInfo effectiveMaxResource;

}
