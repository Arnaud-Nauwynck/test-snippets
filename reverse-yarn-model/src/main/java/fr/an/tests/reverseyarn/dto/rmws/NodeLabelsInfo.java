package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeLabelsInfo
 */
@Data
public class NodeLabelsInfo {

	@XmlElement(name = "nodeLabelInfo")
	private ArrayList<NodeLabelInfo> nodeLabelsInfo = new ArrayList<NodeLabelInfo>();

}
