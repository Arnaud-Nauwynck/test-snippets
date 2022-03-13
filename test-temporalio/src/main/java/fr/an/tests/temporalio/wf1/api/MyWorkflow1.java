package fr.an.tests.temporalio.wf1.api;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MyWorkflow1 {

    @WorkflowMethod
    String runHelloThenGoodBye(String name);

}