package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeToLabelsEntryList
 */
@Data
public class NodeToLabelsEntryList {

	protected ArrayList<NodeToLabelsEntry> nodeToLabels = new ArrayList<NodeToLabelsEntry>();

}
