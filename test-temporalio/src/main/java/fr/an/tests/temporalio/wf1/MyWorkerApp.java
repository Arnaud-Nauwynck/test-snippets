package fr.an.tests.temporalio.wf1;

import fr.an.tests.temporalio.wf1.api.MyWfConsts;
import fr.an.tests.temporalio.wf1.impl.MySimpleActivityImpl;
import fr.an.tests.temporalio.wf1.impl.MyWorkflow1Impl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class MyWorkerApp {

    public static void main(String[] args) {
        // This gRPC stubs wrapper talks to the local docker instance of the Temporal service.
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowClient client = WorkflowClient.newInstance(service);
        // Create a Worker factory that can be used to create Workers that poll specific Task Queues.
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(MyWfConsts.MY_TASK_QUEUE_1);
        // This Worker hosts both Workflow and Activity implementations.
        // Workflows are stateful, so you need to supply a type to create instances.
        worker.registerWorkflowImplementationTypes(MyWorkflow1Impl.class);
        // Activities are stateless and thread safe, so a shared instance is used.
        worker.registerActivitiesImplementations(new MySimpleActivityImpl());

        // Start polling the Task Queue.
        factory.start();
    }
}