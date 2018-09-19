package fr.an.tests.testrestserver.domain;

import java.util.ArrayList;
import java.util.List;

public class Srv1A {

	public final int id;
	
	public String srv1Name;
	
	public List<Srv1B> bs = new ArrayList<>();

	public Srv1A(int id, String srv1Name) {
		super();
		this.id = id;
		this.srv1Name = srv1Name;
	}
	
}
