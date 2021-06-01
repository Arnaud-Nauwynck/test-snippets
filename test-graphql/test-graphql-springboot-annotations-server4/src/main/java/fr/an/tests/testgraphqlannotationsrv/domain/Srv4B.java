package fr.an.tests.testgraphqlannotationsrv.domain;

import graphql.annotations.annotationTypes.GraphQLField;

public class Srv4B {

	@GraphQLField
	public final int id;
	
	@GraphQLField
	private String srv2Name;
	
	@GraphQLField
	private Srv4A a;

	public Srv4B(int id, String srv2Name) {
		this.id = id;
		this.srv2Name = srv2Name;
	}

	public String getSrv2Name() {
		return srv2Name;
	}
	
	public void setSrv2Name(String p) {
		this.srv2Name = p;
	}
	
	public Srv4A getA() {
		return a;
	}

	public void setA(Srv4A a) {
		this.a = a;
	}
	
	
}
