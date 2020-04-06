package fr.an.tests.testmockito.annotedctorbeans;

import javax.inject.Inject;

public class AnnotedCtorA {
	
	private final AnnotedCtorB b;

	@Inject
	public AnnotedCtorA(AnnotedCtorB b) {
		this.b = b;
	}

	public String foo(String x) {
		return "A.foo(" + b.foo(x) + ")";
	}
}