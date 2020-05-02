package fr.an.tests.helix;

import org.apache.helix.manager.zk.ZKHelixAdmin;
import org.apache.helix.manager.zk.ZNRecordSerializer;
import org.apache.helix.manager.zk.ZkClient;
import org.apache.helix.model.IdealState.RebalanceMode;
import org.apache.helix.model.InstanceConfig;
import org.apache.helix.model.StateModelDefinition;

import fr.an.tests.helix.impl.MyStateModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupCluster {
	public static final String DEFAULT_CLUSTER_NAME = "test-helix1";
	
	private String zkAddr = "localhost:2881,localhost:2882";
	private String clusterName = DEFAULT_CLUSTER_NAME;
	private int numNodes = 5;
	private int resources = 1;
	private int partitions = 2;
	private int replicas = 3;
	
	public static final String DEFAULT_STATE_MODEL = "MasterSlave";

	public static void main(String[] args) {
		try {
			SetupCluster app = new SetupCluster();
			app.parseArgs(args);
			app.run();
		} catch (Exception ex) {
			log.error("Failed", ex);
		}		
	}

	private void parseArgs(String[] args) {

	}

	public void run() {
		ZkClient zkclient = null;
		try {
			zkclient = new ZkClient(zkAddr, ZkClient.DEFAULT_SESSION_TIMEOUT, ZkClient.DEFAULT_CONNECTION_TIMEOUT,
					new ZNRecordSerializer());
			ZKHelixAdmin admin = new ZKHelixAdmin(zkclient);

			// add cluster
			admin.addCluster(clusterName, true);

			// add state model definition
			StateModelDefinition stateModelDef = MyStateModel.createDefinition(); 
			admin.addStateModelDef(clusterName, MyStateModel.MYSTATE_MODEL_DEFINITION_NAME, stateModelDef);
			
			// addNodes
			for (int i = 0; i < numNodes; i++) {
				String port = "" + (12001 + i);
				String serverId = "localhost_" + port;
				InstanceConfig config = new InstanceConfig(serverId);
				config.setHostName("localhost");
				config.setPort(port);
				config.setInstanceEnabled(true);
				admin.addInstance(clusterName, config);
			}
			// add resources with partitions
			for(int i = 0; i < resources; i++) {
				String resourceName = "resource" + i;
				admin.addResource(clusterName, resourceName, partitions, 
						MyStateModel.MYSTATE_MODEL_DEFINITION_NAME,
						RebalanceMode.SEMI_AUTO.toString());
				admin.rebalance(clusterName, resourceName, replicas);
			}
			
		} finally {
			if (zkclient != null) {
				zkclient.close();
			}
		}
	}
}
