package fr.an.tests.reverseyarn.dto.app;

import lombok.Data;

@Data
public class ResourceRequest {
	
	public Priority priority;
	public String resourceName;
	public Resource capability;
	int numContainers;
	boolean relaxLocality;
	String nodeLabelExpression;
	ExecutionTypeRequest executionTypeRequest;
	ExecutionType executionType;

}
