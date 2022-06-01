package fr.an.tests.testgraphqljava.domain;

import java.util.ArrayList;
import java.util.List;

public class A {

	public final int id;
	
	public String name;
	
	public List<B> bs = new ArrayList<>();

	public A(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
}
