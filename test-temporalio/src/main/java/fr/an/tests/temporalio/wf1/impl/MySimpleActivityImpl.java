package fr.an.tests.temporalio.wf1.impl;

import fr.an.tests.temporalio.wf1.api.MySimpleActivity;


public class MySimpleActivityImpl implements MySimpleActivity {

	@Override
    public String sayHello(String name) {
    	return "Hello " + name;
    }

	@Override
	public String sayGoodBye(String name, String helloMsg) {
		return ".. Bye " + name;
	}

}
