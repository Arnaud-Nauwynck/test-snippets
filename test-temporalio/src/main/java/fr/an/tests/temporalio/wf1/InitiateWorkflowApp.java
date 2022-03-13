package fr.an.tests.temporalio.wf1;

import fr.an.tests.temporalio.wf1.api.MyWfConsts;
import fr.an.tests.temporalio.wf1.api.MyWorkflow1;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class InitiateWorkflowApp {

    public static void main(String[] args) throws Exception {
        // This gRPC stubs wrapper talks to the local docker instance of the Temporal service.
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
        WorkflowClient client = WorkflowClient.newInstance(service);
        
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(MyWfConsts.MY_TASK_QUEUE_1)
                .build();
        // WorkflowStubs enable calls to methods as if the Workflow object is local, but actually perform an RPC.
        MyWorkflow1 workflow = client.newWorkflowStub(MyWorkflow1.class, options);
        // Synchronously execute the Workflow and wait for the response.
        String greeting = workflow.runHelloThenGoodBye("World");
        System.out.println(greeting);
        
    }
}