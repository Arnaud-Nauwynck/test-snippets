package fr.an.tests.testgraphqlsrv.resolver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import fr.an.tests.testgraphqlsrv.domain.A;
import fr.an.tests.testgraphqlsrv.domain.B;
import fr.an.tests.testgraphqlsrv.repository.ARepository;

@Component
public class RootQueryResolver implements GraphQLQueryResolver {

	@Autowired
	private ARepository aRepository;

	public List<A> a() {
		return aRepository.findAllA();
	}

	public A aById(int id) {
		return aRepository.findAById(id);
	}

	public List<B> b() {
		return aRepository.findAllB();
	}

	public B bById(int id) {
		return aRepository.findBById(id);
	}
	
	
}
