package fr.an.tests.testmockito.annotedctorbeans;

import javax.inject.Inject;

import fr.an.tests.testmockito.C;

public class AnnotedCtorB {
	
	private final C c;
	
	@Inject
	public AnnotedCtorB(C c) {
		this.c = c;
	}

	public String foo(String x) {
		return "B.foo(" + c.foo(x) + ")";
	}
	
}