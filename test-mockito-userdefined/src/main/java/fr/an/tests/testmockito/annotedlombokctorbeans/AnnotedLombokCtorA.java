package fr.an.tests.testmockito.annotedlombokctorbeans;

import javax.inject.Inject;

import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor_={@Inject}) // equivalent to  @Inject public ..A(..B b) { this.b = b; }
// @AllArgsConstructor(onConstructor=@__({@Inject})) // other syntax.. cf https://projectlombok.org/features/constructor 
public class AnnotedLombokCtorA {

	private final AnnotedLombokCtorB b;
	
	public String foo(String x) {
		return "A.foo(" + b.foo(x) + ")";
	}
	
}