package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.FairSchedulerQueueInfoList
 */
@Data
public class FairSchedulerQueueInfoList {

	private ArrayList<FairSchedulerQueueInfo> queue;

}
