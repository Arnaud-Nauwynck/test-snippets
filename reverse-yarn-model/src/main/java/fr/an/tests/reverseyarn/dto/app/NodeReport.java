package fr.an.tests.reverseyarn.dto.app;

import java.util.Set;

public class NodeReport {

	public NodeId nodeId;
	public NodeState nodeState;
	public String httpAddress;
	public String rackName;
	public Resource used;
	public Resource capability;
	public int numContainers;
	public String healthReport;
	public long lastHealthReport;
	public Set<String> nodeLabels;
	public ResourceUtilization containersUtilization;
	public ResourceUtilization nodeUtilization;
	public Integer decommissioningTimeout;
	public NodeUpdateType nodeUpdateType;
	public Set<NodeAttribute> nodeAttributes;

}
