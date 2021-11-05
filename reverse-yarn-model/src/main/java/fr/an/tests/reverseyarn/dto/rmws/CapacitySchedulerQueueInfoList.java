package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.CapacitySchedulerQueueInfoList
 */
@Data
public class CapacitySchedulerQueueInfoList {
	
	protected ArrayList<CapacitySchedulerQueueInfo> queue;

}
