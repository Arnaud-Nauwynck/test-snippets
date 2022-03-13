package fr.an.tests.temporalio;

import io.temporal.testing.TestEnvironmentOptions;
import io.temporal.testing.TestWorkflowEnvironment;

public class TestWorflowEnvironmentApp {

	// TODO does not work.. no gRPC port openned?!
	public static void main(String[] args) {
		TestEnvironmentOptions options = TestEnvironmentOptions.newBuilder()
				.build();
		TestWorkflowEnvironment workflowEnv = TestWorkflowEnvironment.newInstance(options);
		
		workflowEnv.start();
	}

}
