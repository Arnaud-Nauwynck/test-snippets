package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ResourcesInfo
 */
@Data
public class ResourcesInfo {

	protected List<PartitionResourcesInfo> resourceUsagesByPartition = new ArrayList<>();

}
