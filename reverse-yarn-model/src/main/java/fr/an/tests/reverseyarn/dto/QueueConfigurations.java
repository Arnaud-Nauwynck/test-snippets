package fr.an.tests.reverseyarn.dto;

import lombok.Data;

@Data
public class QueueConfigurations {

	public float capacity;
	public float absoluteCapacity;
	public float maxCapacity;
	public float absoluteMaxCapacity;
	public float maxAMPercentage;

	public Resource effectiveMinCapacity;
	public Resource effectiveMaxCapacity;
	public Resource configuredMinResource;
	public Resource configuredMaxResource;

}
