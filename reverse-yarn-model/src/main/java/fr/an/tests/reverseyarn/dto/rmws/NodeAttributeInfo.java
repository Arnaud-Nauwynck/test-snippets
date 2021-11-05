package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeAttributeInfo
 */
@Data
public class NodeAttributeInfo {

	private String prefix;
	private String name;
	private String type;
	private String value;

}
