package fr.an.tests.reverseyarn.dto.rmws;

public enum YarnApplicationState {
	NEW,
	NEW_SAVING,
	SUBMITTED,
	ACCEPTED,
	RUNNING,
	FINISHED,
	FAILED,
	KILLED;
}
