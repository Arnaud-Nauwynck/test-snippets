package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import fr.an.tests.reverseyarn.dto.app.NodeState;
import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeInfo
 */
@Data
public class NodeInfo {

	protected String rack;
	protected NodeState state;
	private String id;
	protected String nodeHostName;
	protected String nodeHTTPAddress;
	private long lastHealthUpdate;
	protected String version;
	protected String healthReport;
	protected int numContainers;
	protected long usedMemoryMB;
	protected long availMemoryMB;
	protected long usedVirtualCores;
	protected long availableVirtualCores;
	private float memUtilization;
	private float cpuUtilization;
	private int numRunningOpportContainers;
	private long usedMemoryOpportGB;
	private long usedVirtualCoresOpport;
	private int numQueuedContainers;
	protected ArrayList<String> nodeLabels = new ArrayList<String>();
	private AllocationTagsInfo allocationTags;
	protected ResourceUtilizationInfo resourceUtilization;
	protected ResourceInfo usedResource;
	protected ResourceInfo availableResource;
	protected NodeAttributesInfo nodeAttributesInfo;
	private ResourceInfo totalResource;
}
