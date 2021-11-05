package fr.an.tests.reverseyarn.dto.app;

public class ContainerReport {

	public ContainerId containerId;
	public Resource allocatedResource;
	public NodeId assignedNode;
	public Priority priority;
	public long creationTime;
	public long finishTime;
	public String diagnosticsInfo;
	public String logUrl;
	public ContainerState containerState;
	public int containerExitStatus$;
	public String nodeHttpAddress;

}
