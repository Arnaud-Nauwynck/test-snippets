package fr.an.tests.reverseyarn.dto.rmws;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState
 */
public enum RMAppAttemptState {
	  NEW, SUBMITTED, SCHEDULED, ALLOCATED, LAUNCHED, FAILED, RUNNING, FINISHING, 
	  FINISHED, KILLED, ALLOCATED_SAVING, LAUNCHED_UNMANAGED_SAVING, FINAL_SAVING

}
