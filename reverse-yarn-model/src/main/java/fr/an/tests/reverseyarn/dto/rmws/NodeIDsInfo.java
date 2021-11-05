package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.NodeIDsInfo
 */
@Data
public class NodeIDsInfo {

	@XmlElement(name = "nodes")
	protected ArrayList<String> nodeIDsList = new ArrayList<String>();

	@XmlElement(name = "partitionInfo")
	private PartitionInfo partitionInfo;

}
