package fr.an.tests.helix;

import org.apache.helix.HelixManager;
import org.apache.helix.controller.HelixControllerMain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartClusterManager {

	private String zkAddr = "localhost:2881,localhost:2882";
	private String clusterName = SetupCluster.DEFAULT_CLUSTER_NAME;

	public static void main(String[] args) {
		try {
			StartClusterManager app = new StartClusterManager();
			app.parseArgs(args);
			app.run();
		} catch (Exception ex) {
			log.error("Failed", ex);
		}
	}

	private void parseArgs(String[] args) {

	}

	public void run() throws Exception {
		final HelixManager manager = HelixControllerMain.startHelixController(zkAddr, clusterName, null,
				HelixControllerMain.STANDALONE);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Shutting down cluster manager: " + manager.getInstanceName());
				manager.disconnect();
			}
		});

		Thread.currentThread().join();
	}
}
