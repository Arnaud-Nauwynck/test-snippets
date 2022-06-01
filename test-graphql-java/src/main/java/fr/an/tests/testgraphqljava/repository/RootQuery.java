package fr.an.tests.testgraphqljava.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.an.tests.testgraphqljava.domain.A;
import fr.an.tests.testgraphqljava.domain.B;

public class RootQuery {

	private int aIdGenerator = 1;
	private int bIdGenerator = 1;
	private Map<Integer,A> aById = new HashMap<>();
	private Map<Integer,B> bById = new HashMap<>();
	
	public RootQuery() {
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
