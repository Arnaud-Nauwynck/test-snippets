package fr.an.tests.testgraphqlannotationsrv.domain;

import java.util.ArrayList;
import java.util.List;

import graphql.annotations.annotationTypes.GraphQLField;

public class Srv3A {

	@GraphQLField
	public final int id;
	
	@GraphQLField
	public String srv3Name;
	
	@GraphQLField
	public List<Srv3B> bs = new ArrayList<>();

	public Srv3A(int id, String srv2Name) {
		super();
		this.id = id;
		this.srv3Name = srv2Name;
	}
	
}
