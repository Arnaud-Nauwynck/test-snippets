package fr.an.tests.testmockito.beans;

import javax.inject.Inject;

public class A {

	@Inject
	private B b;
	
	public String foo(String x) {
		return "A.foo(" + b.foo(x) + ")";
	}
	
}