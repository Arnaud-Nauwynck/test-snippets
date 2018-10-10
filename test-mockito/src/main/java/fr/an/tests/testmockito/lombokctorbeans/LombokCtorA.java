package fr.an.tests.testmockito.lombokctorbeans;

import lombok.AllArgsConstructor;

@AllArgsConstructor // equivalent of  A(BAnnotedLombokCtorBean b) { this.b = b; }
public class LombokCtorA {

	private final LombokCtorB b;
	
	public String foo(String x) {
		return "A.foo(" + b.foo(x) + ")";
	}
	
}