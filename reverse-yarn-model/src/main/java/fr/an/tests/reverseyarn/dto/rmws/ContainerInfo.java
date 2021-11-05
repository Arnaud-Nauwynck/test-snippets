package fr.an.tests.reverseyarn.dto.rmws;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.nodemanager.webapp.dao.ContainerInfo
 */
@Data
public class ContainerInfo {

	protected String id;
	protected String state;
	protected int exitCode;
	protected String diagnostics;
	protected String user;
	protected long totalMemoryNeededMB;
	protected long totalVCoresNeeded;
	private String executionType;
	protected String containerLogsLink;
	protected String nodeId;

	@XmlTransient
	protected String containerLogsShortLink;
	@XmlTransient
	protected String exitStatus;

	protected List<String> containerLogFiles;

}
