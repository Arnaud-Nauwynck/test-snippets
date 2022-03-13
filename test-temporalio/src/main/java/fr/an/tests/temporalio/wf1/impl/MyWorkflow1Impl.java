package fr.an.tests.temporalio.wf1.impl;

import java.time.Duration;

import fr.an.tests.temporalio.wf1.api.MySimpleActivity;
import fr.an.tests.temporalio.wf1.api.MyWorkflow1;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

public class MyWorkflow1Impl implements MyWorkflow1 {

    ActivityOptions options = ActivityOptions.newBuilder()
            .setScheduleToCloseTimeout(Duration.ofMinutes(5))
            .build();

    // ActivityStubs enable calls to Activities as if they are local methods, but actually perform an RPC.
    private final MySimpleActivity myActivitiy1 = Workflow.newActivityStub(MySimpleActivity.class, options);

    @Override
    public String runHelloThenGoodBye(String name) {
        // This is the entry point to the Workflow.
        // If there were other Activity methods they would be orchestrated here or from within other Activities.
        String helloMsg = myActivitiy1.sayHello(name);
        
        String byeMsg = myActivitiy1.sayGoodBye(name, helloMsg);
        
        return byeMsg;
    }
}