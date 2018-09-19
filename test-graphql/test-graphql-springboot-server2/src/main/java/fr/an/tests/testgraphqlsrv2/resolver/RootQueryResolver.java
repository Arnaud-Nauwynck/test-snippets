package fr.an.tests.testgraphqlsrv2.resolver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import fr.an.tests.testgraphqlsrv2.domain.Srv2A;
import fr.an.tests.testgraphqlsrv2.domain.Srv2B;
import fr.an.tests.testgraphqlsrv2.repository.ARepository;

@Component
public class RootQueryResolver implements GraphQLQueryResolver {

	@Autowired
	private ARepository aRepository;

	public List<Srv2A> a() {
		return aRepository.findAllA();
	}

	public Srv2A aById(int id) {
		return aRepository.findAById(id);
	}

	public List<Srv2B> b() {
		return aRepository.findAllB();
	}

	public Srv2B bById(int id) {
		return aRepository.findBById(id);
	}
	
	
}
