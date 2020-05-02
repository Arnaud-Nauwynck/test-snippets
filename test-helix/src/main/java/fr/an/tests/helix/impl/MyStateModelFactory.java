package fr.an.tests.helix.impl;

import org.apache.helix.HelixManager;
import org.apache.helix.participant.statemachine.StateModelFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyStateModelFactory extends StateModelFactory<MyStateModel> {
	
	private final HelixManager manager;

	public MyStateModelFactory(HelixManager manager) {
		this.manager = manager;
	}

	@Override
	public MyStateModel createNewStateModel(String resource, String partition) {
		log.info("createNewStateModel resource:" + resource + ", partition:" + partition);
		// String checkResourceName = partition.split("_")[0];
		MyStateModel model = new MyStateModel(manager, resource, partition);
		return model;
	}
	
}
