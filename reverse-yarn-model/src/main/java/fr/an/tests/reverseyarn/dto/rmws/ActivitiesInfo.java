package fr.an.tests.reverseyarn.dto.rmws;

import java.util.List;

import lombok.Data;

@Data
public class ActivitiesInfo {

	private String nodeId;
	private Long timestamp;
	private String dateTime;
	private String diagnostic;
	private List<NodeAllocationInfo> allocations;

}
