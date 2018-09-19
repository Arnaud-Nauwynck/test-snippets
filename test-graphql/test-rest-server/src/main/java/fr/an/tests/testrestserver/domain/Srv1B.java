package fr.an.tests.testrestserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Srv1B {

	public final int id;
	
	public String srv1Name;
	
	@JsonIgnore
	public Srv1A a;

	public Srv1B(int id, String srv1Name) {
		this.id = id;
		this.srv1Name = srv1Name;
	}
	
	
}
