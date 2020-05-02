package fr.an.tests.helix;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import org.apache.helix.HelixManager;
import org.apache.helix.HelixManagerFactory;
import org.apache.helix.InstanceType;
import org.apache.helix.manager.zk.ZKHelixAdmin;
import org.apache.helix.manager.zk.ZNRecordSerializer;
import org.apache.helix.manager.zk.ZkClient;
import org.apache.helix.model.InstanceConfig;
import org.apache.helix.participant.StateMachineEngine;

import fr.an.tests.helix.impl.MyStateModel;
import fr.an.tests.helix.impl.MyStateModelFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
public class MyParticipantApp {

	private String zkAddr = "localhost:2881,localhost:2882";
	private String clusterName = SetupCluster.DEFAULT_CLUSTER_NAME;
	private String instanceName; // "localhost_12001", "localhost_12002", ..

	ServerSocket serverSock;
	
	public static void main(String[] args) {
		try {
			MyParticipantApp app = new MyParticipantApp();
			app.parseArgs(args);
			app.run();
		} catch (Exception ex) {
			log.error("Failed", ex);
		}
	}

	private void parseArgs(String[] args) {
		// find free port for unique instanceid
		int port = 12001;
		log.info("find free port to listen in range [" + port + ", ...)");
		for(;;) {
			try {
				ServerSocket serverSock = new ServerSocket(port);
				
				this.serverSock = serverSock;
				this.instanceName = "localhost_" + port;
				break;
			} catch(Exception ex) {
				// failed to create server socket... port not free, try next one
				port++;
			}
		}
		log.info("listen on port " + port);
		log.info("aquire instanceName: " + instanceName);
	}

	public void run() throws Exception {
		ZkClient zkclient = null;
		try {
			// add node to cluster if not already added
			zkclient = new ZkClient(zkAddr, ZkClient.DEFAULT_SESSION_TIMEOUT, ZkClient.DEFAULT_CONNECTION_TIMEOUT,
					new ZNRecordSerializer());
			ZKHelixAdmin admin = new ZKHelixAdmin(zkclient);

			List<String> nodes = admin.getInstancesInCluster(clusterName);
			if (!nodes.contains(instanceName)) {
				InstanceConfig config = new InstanceConfig(instanceName);
				config.setHostName("localhost");
				config.setInstanceEnabled(true);
				admin.addInstance(clusterName, config);
			}

			HelixManager manager = HelixManagerFactory.getZKHelixManager(clusterName, instanceName,
					InstanceType.PARTICIPANT, zkAddr);

			StateMachineEngine stateMach = manager.getStateMachineEngine();
			MyStateModelFactory modelFactory = new MyStateModelFactory(manager); // instanceName
			stateMach.registerStateModelFactory(MyStateModel.MYSTATE_MODEL_DEFINITION_NAME, modelFactory);

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					System.out.println("Shutting down " + instanceName);
					disconnect(manager);
				}
			});

			connectAndWait(manager);

		} finally {
			if (zkclient != null) {
				zkclient.close();
			}
		}
	}

	private void connectAndWait(HelixManager manager) {
		try {
			manager.connect();

			Thread.currentThread().join();
		} catch (InterruptedException e) {
			System.err.println(" [-] " + instanceName + " is interrupted ...");
		} catch (Exception ex) {
			log.error("Failed", ex);
		} finally {
			disconnect(manager);
		}
	}

	private void disconnect(HelixManager manager) {
		if (manager != null) {
			manager.disconnect();
		}
		if (serverSock != null) {
			try {
				serverSock.close();
			} catch (IOException e) {
			}
			this.serverSock = null;
		}
	}

}
