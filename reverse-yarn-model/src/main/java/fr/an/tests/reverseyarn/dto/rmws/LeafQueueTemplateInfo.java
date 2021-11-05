package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.LeafQueueTemplateInfo
 *
 */
@Data
public class LeafQueueTemplateInfo {

	private ArrayList<ConfItem> items = new ArrayList<>();

	/**
	 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.LeafQueueTemplateInfo.ConfItem
	 */
	@Data
	public static class ConfItem {
		private String name;
		private String value;
	}

}
