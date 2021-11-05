package fr.an.tests.reverseyarn.dto.rmws;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.LabelsToNodesInfo
 */
@Data
public class LabelsToNodesInfo {

	protected Map<NodeLabelInfo, NodeIDsInfo> labelsToNodes = new HashMap<NodeLabelInfo, NodeIDsInfo>();

}
