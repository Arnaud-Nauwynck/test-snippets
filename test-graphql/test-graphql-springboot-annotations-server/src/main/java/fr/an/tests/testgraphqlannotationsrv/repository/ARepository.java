package fr.an.tests.testgraphqlannotationsrv.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import fr.an.tests.testgraphqlannotationsrv.domain.Srv3A;
import fr.an.tests.testgraphqlannotationsrv.domain.Srv3B;

@Component
public class ARepository {

	private int aIdGenerator = 1;
	private int bIdGenerator = 1;
	private Map<Integer,Srv3A> aById = new HashMap<>();
	private Map<Integer,Srv3B> bById = new HashMap<>();
	
	public ARepository() {
		for (int i = 1; i < 10; i++) {
			int idA = aIdGenerator++;
			Srv3A ai = new Srv3A(idA, "a" + idA);
			aById.put(ai.id, ai);
			for(int j = 1; j < 10; j++) {
				int bId = bIdGenerator++;
				Srv3B bj = new Srv3B(bId, ai.srv3Name + "-" + "b" + bId);
				bj.setA(ai);
				ai.bs.add(bj);
				bById.put(bj.id, bj);
			}
		}
	}
	
	public List<Srv3A> findAllA() {
		return new ArrayList<>(aById.values());
	}

	public Srv3A findAById(int id) {
		return aById.get(id);
	}

	public List<Srv3B> findAllB() {
		return new ArrayList<>(bById.values());
	}

	public Srv3B findBById(int id) {
		return bById.get(id);
	}

}
