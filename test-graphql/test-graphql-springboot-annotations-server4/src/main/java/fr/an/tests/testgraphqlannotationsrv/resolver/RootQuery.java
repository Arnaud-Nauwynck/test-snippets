package fr.an.tests.testgraphqlannotationsrv.resolver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.an.tests.testgraphqlannotationsrv.domain.Srv4A;
import fr.an.tests.testgraphqlannotationsrv.domain.Srv4B;
import fr.an.tests.testgraphqlannotationsrv.repository.ARepository;
import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;

@Component
@GraphQLDescription("Query for testing GraphQL with annotations..")
public class RootQuery {

	@Autowired
	private ARepository aRepository;

	@GraphQLField
	public List<Srv4A> a() {
		return aRepository.findAllA();
	}

	@GraphQLField
	public Srv4A aById(@GraphQLName("id") int id) {
		return aRepository.findAById(id);
	}

	@GraphQLField
	public List<Srv4B> b() {
		return aRepository.findAllB();
	}

	@GraphQLField
	public Srv4B bById(@GraphQLName("id") int id) {
		return aRepository.findBById(id);
	}
	
}
