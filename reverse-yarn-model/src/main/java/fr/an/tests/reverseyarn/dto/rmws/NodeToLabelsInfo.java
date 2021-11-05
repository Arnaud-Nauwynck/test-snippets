package fr.an.tests.reverseyarn.dto.rmws;

import java.util.HashMap;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeToLabelsInfo
 */
@Data
public class NodeToLabelsInfo {

	private HashMap<String, NodeLabelsInfo> nodeToLabels = new HashMap<String, NodeLabelsInfo>();

}
