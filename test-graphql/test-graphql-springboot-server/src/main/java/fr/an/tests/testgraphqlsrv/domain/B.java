package fr.an.tests.testgraphqlsrv.domain;

public class B {

	public final int id;
	
	private String name;
	
	private A a;

	public B(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public A getA() {
		return a;
	}

	public void setA(A a) {
		this.a = a;
	}
	
	
}
