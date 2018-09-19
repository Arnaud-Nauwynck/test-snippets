package fr.an.tests.testgraphqlsrv.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import fr.an.tests.testgraphqlsrv.domain.A;
import fr.an.tests.testgraphqlsrv.domain.B;

@Component
public class ARepository {

	private int aIdGenerator = 1;
	private int bIdGenerator = 1;
	private Map<Integer,A> aById = new HashMap<>();
	private Map<Integer,B> bById = new HashMap<>();
	
	public ARepository() {
		for (int i = 1; i < 10; i++) {
			int idA = aIdGenerator++;
			A ai = new A(idA, "a" + idA);
			aById.put(ai.id, ai);
			for(int j = 1; j < 10; j++) {
				int bId = bIdGenerator++;
				B bj = new B(bId, ai.name + "-" + "b" + bId);
				bj.setA(ai);
				ai.bs.add(bj);
				bById.put(bj.id, bj);
			}
		}
	}
	
	public List<A> findAllA() {
		return new ArrayList<>(aById.values());
	}

	public A findAById(int id) {
		return aById.get(id);
	}

	public List<B> findAllB() {
		return new ArrayList<>(bById.values());
	}

	public B findBById(int id) {
		return bById.get(id);
	}

}
