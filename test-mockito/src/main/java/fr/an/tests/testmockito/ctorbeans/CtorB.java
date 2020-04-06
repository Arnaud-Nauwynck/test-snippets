package fr.an.tests.testmockito.ctorbeans;

import fr.an.tests.testmockito.C;

public class CtorB {
	
	private final C c;
	
	public CtorB(C c) {
		this.c = c;
	}

	public String foo(String x) {
		return "B.foo(" + c.foo(x) + ")";
	}
	
}