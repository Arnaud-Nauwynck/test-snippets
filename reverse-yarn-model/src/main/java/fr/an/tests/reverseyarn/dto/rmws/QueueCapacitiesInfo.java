package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.QueueCapacitiesInfo
 */
@Data
public class QueueCapacitiesInfo {

	protected List<PartitionQueueCapacitiesInfo> queueCapacitiesByPartition = new ArrayList<>();

}
