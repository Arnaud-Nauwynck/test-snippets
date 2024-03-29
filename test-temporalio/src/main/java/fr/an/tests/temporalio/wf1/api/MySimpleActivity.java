package fr.an.tests.temporalio.wf1.api;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface MySimpleActivity {

    @ActivityMethod
    String sayHello(String name);

    @ActivityMethod
    String sayGoodBye(String name, String helloMsg);

}

