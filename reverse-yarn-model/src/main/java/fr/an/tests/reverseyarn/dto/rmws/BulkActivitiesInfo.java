package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.BulkActivitiesInfo
 */
@Data
public class BulkActivitiesInfo {

	private List<ActivitiesInfo> activities = new ArrayList<>();

}
