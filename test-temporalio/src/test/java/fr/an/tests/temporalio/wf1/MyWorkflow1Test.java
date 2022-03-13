package fr.an.tests.temporalio.wf1;


import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.junit.Rule;
import org.junit.Test;

import fr.an.tests.temporalio.wf1.api.MySimpleActivity;
import fr.an.tests.temporalio.wf1.api.MyWorkflow1;
import fr.an.tests.temporalio.wf1.impl.MySimpleActivityImpl;
import fr.an.tests.temporalio.wf1.impl.MyWorkflow1Impl;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowRule;

public class MyWorkflow1Test {

    @Rule
    public TestWorkflowRule testWorkflowRule =
            TestWorkflowRule.newBuilder()
                    .setWorkflowTypes(MyWorkflow1Impl.class)
                    .setDoNotStart(true)
                    .build();

    @Test
    public void testGetGreeting() {
        testWorkflowRule.getWorker().registerActivitiesImplementations(new MySimpleActivityImpl());
        testWorkflowRule.getTestEnvironment().start();

        MyWorkflow1 workflow = testWorkflowRule
                        .getWorkflowClient()
                        .newWorkflowStub(
                                MyWorkflow1.class,
                                WorkflowOptions.newBuilder().setTaskQueue(testWorkflowRule.getTaskQueue()).build());
        String greeting = workflow.getGreeting("John");
        assertEquals("Hello John", greeting);
        testWorkflowRule.getTestEnvironment().shutdown();
    }

    @Test
    public void testMockedGetGreeting() {
    	MySimpleActivity formatActivities = mock(MySimpleActivity.class, withSettings().withoutAnnotations());
        when(formatActivities.composeGreeting(anyString())).thenReturn("Hello World!");
        testWorkflowRule.getWorker().registerActivitiesImplementations(formatActivities);
        testWorkflowRule.getTestEnvironment().start();

        MyWorkflow1 workflow =
                testWorkflowRule
                        .getWorkflowClient()
                        .newWorkflowStub(
                                MyWorkflow1.class,
                                WorkflowOptions.newBuilder().setTaskQueue(testWorkflowRule.getTaskQueue()).build());
        String greeting = workflow.getGreeting("World");
        assertEquals("Hello World!", greeting);
        testWorkflowRule.getTestEnvironment().shutdown();
    }
}