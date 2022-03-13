package fr.an.tests.temporalio.wf1.impl;

import fr.an.tests.temporalio.wf1.api.MySimpleActivity;


public class MySimpleActivityImpl implements MySimpleActivity {

	@Override
    public String composeGreeting(String name) {
    	return "Hello " + name;
    }

}
