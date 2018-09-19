package fr.an.tests.testgraphqlsrv2.domain;

public class Srv2B {

	public final int id;
	
	private String srv2Name;
	
	private Srv2A a;

	public Srv2B(int id, String srv2Name) {
		this.id = id;
		this.srv2Name = srv2Name;
	}

	public String getSrv2Name() {
		return srv2Name;
	}
	
	public void setSrv2Name(String p) {
		this.srv2Name = p;
	}
	
	public Srv2A getA() {
		return a;
	}

	public void setA(Srv2A a) {
		this.a = a;
	}
	
	
}
