package fr.an.tests.testgraphqlsrv2.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import fr.an.tests.testgraphqlsrv2.domain.Srv2A;
import fr.an.tests.testgraphqlsrv2.domain.Srv2B;

@Component
public class ARepository {

	private int aIdGenerator = 1;
	private int bIdGenerator = 1;
	private Map<Integer,Srv2A> aById = new HashMap<>();
	private Map<Integer,Srv2B> bById = new HashMap<>();
	
	public ARepository() {
		for (int i = 1; i < 10; i++) {
			int idA = aIdGenerator++;
			Srv2A ai = new Srv2A(idA, "a" + idA);
			aById.put(ai.id, ai);
			for(int j = 1; j < 10; j++) {
				int bId = bIdGenerator++;
				Srv2B bj = new Srv2B(bId, ai.srv2Name + "-" + "b" + bId);
				bj.setA(ai);
				ai.bs.add(bj);
				bById.put(bj.id, bj);
			}
		}
	}
	
	public List<Srv2A> findAllA() {
		return new ArrayList<>(aById.values());
	}

	public Srv2A findAById(int id) {
		return aById.get(id);
	}

	public List<Srv2B> findAllB() {
		return new ArrayList<>(bById.values());
	}

	public Srv2B findBById(int id) {
		return bById.get(id);
	}

}
