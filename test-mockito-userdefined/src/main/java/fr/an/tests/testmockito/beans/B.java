package fr.an.tests.testmockito.beans;

import javax.inject.Inject;

import fr.an.tests.testmockito.C;

public class B {
	
	@Inject
	private C c;
	
	public String foo(String x) {
		return "B.foo(" + c.foo(x) + ")";
	}
	
}