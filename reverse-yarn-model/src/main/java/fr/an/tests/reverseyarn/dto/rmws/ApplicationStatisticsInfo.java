package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ApplicationStatisticsInfo
 */
@Data
public class ApplicationStatisticsInfo {

	protected List<StatisticsItemInfo> statItem = new ArrayList<StatisticsItemInfo>();

}
