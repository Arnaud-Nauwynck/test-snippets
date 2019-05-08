package fr.an.tests.testmockito.ctorbeans;

public class CtorA {
	
	private final CtorB b;
	
	public CtorA(CtorB b) {
		this.b = b;
	}

	public String foo(String x) {
		return "A.foo(" + b.foo(x) + ")";
	}
}