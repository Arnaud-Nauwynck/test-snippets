package fr.an.tests.reverseyarn.dto.rmws;

// import org.apache.hadoop.ha.HAServiceProtocol;
// import org.apache.hadoop.service.Service.STATE;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ClusterInfo
 */
@Data
public class ClusterInfo {

	  protected long id;
	  protected long startedOn;
	  protected STATE state;
	  protected HAServiceState haState;
	  protected String rmStateStoreName;
	  protected String resourceManagerVersion;
	  protected String resourceManagerBuildVersion;
	  protected String resourceManagerVersionBuiltOn;
	  protected String hadoopVersion;
	  protected String hadoopBuildVersion;
	  protected String hadoopVersionBuiltOn;
	  protected String haZooKeeperConnectionState;

}
