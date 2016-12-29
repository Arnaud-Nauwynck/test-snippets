package fr.an.tests.detectobjectcycle;

import java.util.ArrayList;
import java.util.List;

public class Foo1 {

	private Foo2 foo2;
	private List<Foo3> foos3 = new ArrayList<>();
	
	public Foo2 getFoo2() {
		return foo2;
	}
	public void setFoo2(Foo2 foo2) {
		this.foo2 = foo2;
	}
	public List<Foo3> getFoos3() {
		return foos3;
	}
	public void setFoos3(List<Foo3> foos3) {
		this.foos3 = foos3;
	}
	
	
}
