package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ExecutionTypeRequestInfo
 */
@Data
public class ExecutionTypeRequestInfo {

	private String executionType;

	private boolean enforceExecutionType;

}
