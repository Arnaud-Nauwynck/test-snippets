package fr.an.tests.helix.impl;

import java.util.concurrent.CountDownLatch;

import org.apache.helix.HelixDefinedState;
import org.apache.helix.HelixManager;
import org.apache.helix.NotificationContext;
import org.apache.helix.model.InstanceConfig;
import org.apache.helix.model.Message;
import org.apache.helix.model.StateModelDefinition;
import org.apache.helix.participant.statemachine.StateModel;
import org.apache.helix.participant.statemachine.StateModelInfo;
import org.apache.helix.participant.statemachine.Transition;

import lombok.extern.slf4j.Slf4j;

@StateModelInfo(initialState = "OFFLINE", states = { "OFFLINE", "INACTIVE", "BOOTSTRAP", "SLAVE", "MASTER", })
@Slf4j
public class MyStateModel extends StateModel {

	public static final String MYSTATE_MODEL_DEFINITION_NAME = "MyStateModel";

	private final HelixManager _manager;
	private final String _resource;
	private final String _partition;
	private final String _serverId;

	private InstanceConfig instanceConfig;

	CountDownLatch latchBootstrap = new CountDownLatch(1);
	
	public MyStateModel(HelixManager manager, String resource, String partition) {
		this._manager = manager;
		this._resource = resource;
		this._partition = partition;

		String clusterName = manager.getClusterName();
		String instanceName = manager.getInstanceName();
		this._serverId = instanceName;
		this.instanceConfig = manager.getClusterManagmentTool().getInstanceConfig(clusterName, instanceName);
	}

	/**
	 * <pre>
	 *
	 *                  <--- INACTIVE <----
	 *                 |                   |
	 * DROPPED <--- OFFLINE              SLAVE  <----> MASTER
	 *                 |                   |
	 *                  ---> BOOTSTRAP --->
	 * 
	 * </pre>
	 * 
	 * @return
	 */
	public static StateModelDefinition createDefinition() {
		StateModelDefinition.Builder builder = new StateModelDefinition.Builder(MYSTATE_MODEL_DEFINITION_NAME);
		// Init state
		builder.initialState(MyState.OFFLINE.name());

		/*
		 * States and their priority which are managed by Helix controller.
		 *
		 * The implication is that transition to a state which has a higher number is
		 * considered a "downward" transition by Helix. Downward transitions are not
		 * throttled.
		 */
		builder.addState(MyState.MASTER.name(), 0);
		builder.addState(MyState.SLAVE.name(), 1);
		builder.addState(MyState.BOOTSTRAP.name(), 2);
		builder.addState(MyState.INACTIVE.name(), 2);
		builder.addState(MyState.OFFLINE.name(), 3);
		for (HelixDefinedState state : HelixDefinedState.values()) { // ERROR DROPPED
			builder.addState(state.name());
		}

		// Add valid transitions between the states.
		builder.addTransition(MyState.OFFLINE.name(), MyState.BOOTSTRAP.name(), 3);
		builder.addTransition(MyState.BOOTSTRAP.name(), MyState.SLAVE.name(), 2);
		builder.addTransition(MyState.SLAVE.name(), MyState.MASTER.name(), 1);
		builder.addTransition(MyState.MASTER.name(), MyState.SLAVE.name(), 0);
		builder.addTransition(MyState.SLAVE.name(), MyState.INACTIVE.name(), 2);
		builder.addTransition(MyState.INACTIVE.name(), MyState.OFFLINE.name(), 3);
		builder.addTransition(MyState.OFFLINE.name(), HelixDefinedState.DROPPED.name());

		// States constraints
		builder.upperBound(MyState.MASTER.name(), 1);

		// Dynamic constraints
		// "R" means it should be derived based on the replication factor for the
		// cluster resource
		builder.dynamicUpperBound(MyState.SLAVE.name(), "R");

		return builder.build();
	}

	private void logMsgTransition(Message message, NotificationContext context) {
		System.out.println();
		log.info(_serverId + " transitioning from " + message.getFromState() + " to " + message.getToState() + " for "
				+ _resource + " partition:" + _partition);
		System.out.println();
	}

	@Transition(from = "OFFLINE", to = "BOOTSTRAP")
	public void onBecomeBootstrapFromOffline(Message message, NotificationContext context) throws Exception {
		logMsgTransition(message, context);

		new Thread() {
			public void run() {
				log.info("######### Emulate loading data to sync with master .. sleep 15s");
				try {
					Thread.sleep(15*1000);
				} catch (InterruptedException e) {
				}
				log.info("######### .. done sleep 15s, Emulate now in sync with master => unlock BOOTSTRAP for SLAVE");
				latchBootstrap.countDown();
			}
		}.start();
	}

	@Transition(from = "BOOTSTRAP", to = "SLAVE")
	public void onBecomeSlaveFromBootstrap(Message message, NotificationContext context) throws Exception {
		// cf LinkedIn Ambry
		// blocked by a latch... to notify that replica is now in sync, and ready to become SLAVE
		log.info("######### candidate for BOOTSTRAP->SLAVE ... waiting for latch");
		latchBootstrap.await();

		logMsgTransition(message, context);

	}

	@Transition(from = "SLAVE", to = "MASTER")
	public void onBecomeMasterFromSlave(Message message, NotificationContext context) throws Exception {
		logMsgTransition(message, context);

//    ZkHelixPropertyStore<ZNRecord> helixPropertyStore = context.getManager().getHelixPropertyStore();

	}

	@Transition(from = "MASTER", to = "SLAVE")
	public void onBecomeSlaveFromMaster(Message message, NotificationContext context) throws Exception {
		logMsgTransition(message, context);
	}

	@Transition(from = "SLAVE", to = "INACTIVE")
	public void onBecomeOfflineFromSlave(Message message, NotificationContext context) {
		logMsgTransition(message, context);
	}

	@Transition(from = "INACTIVE", to = "OFFLINE")
	public void onBecomeOfflineFromInactive(Message message, NotificationContext context) {
		logMsgTransition(message, context);
	}

	// @Transition(from = "OFFLINE", to = "DROPPED")
	public void onBecomeDroppedFromOffline(Message message, NotificationContext context) {
		logMsgTransition(message, context);
	}

	@Override
	public void reset() {
		log.warn("Default reset() invoked");
	}

}
