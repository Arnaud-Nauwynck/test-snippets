package fr.an.tests.testgraphqlannotationsrv.domain;

import java.util.ArrayList;
import java.util.List;

import graphql.annotations.annotationTypes.GraphQLField;

public class Srv4A {

	@GraphQLField
	public final int id;
	
	@GraphQLField
	public String srv4Name;
	
	@GraphQLField
	public List<Srv4B> bs = new ArrayList<>();

	public Srv4A(int id, String srv2Name) {
		super();
		this.id = id;
		this.srv4Name = srv2Name;
	}
	
}
