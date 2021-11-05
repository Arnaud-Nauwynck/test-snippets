package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeToLabelsEntry
 */
@Data
public class NodeToLabelsEntry {

	private String nodeId;

	private ArrayList<String> labels = new ArrayList<String>();

}
