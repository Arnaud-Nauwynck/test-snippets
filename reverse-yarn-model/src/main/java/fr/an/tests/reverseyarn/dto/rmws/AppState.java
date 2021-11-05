package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppState
 */
@Data
public class AppState {

	  String state;
	  String diagnostics;

}
