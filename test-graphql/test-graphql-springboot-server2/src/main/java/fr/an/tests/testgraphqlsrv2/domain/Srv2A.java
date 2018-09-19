package fr.an.tests.testgraphqlsrv2.domain;

import java.util.ArrayList;
import java.util.List;

public class Srv2A {

	public final int id;
	
	public String srv2Name;
	
	public List<Srv2B> bs = new ArrayList<>();

	public Srv2A(int id, String srv2Name) {
		super();
		this.id = id;
		this.srv2Name = srv2Name;
	}
	
}
