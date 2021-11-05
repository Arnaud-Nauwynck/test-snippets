package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import fr.an.tests.reverseyarn.dto.rmws.LeafQueueTemplateInfo.ConfItem;
import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AutoQueueTemplatePropertiesInfo
 */
@Data
public class AutoQueueTemplatePropertiesInfo {

	private ArrayList<ConfItem> property = new ArrayList<>();

}
