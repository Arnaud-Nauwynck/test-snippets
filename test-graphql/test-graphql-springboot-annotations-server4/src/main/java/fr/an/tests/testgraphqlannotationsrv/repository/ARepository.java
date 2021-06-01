package fr.an.tests.testgraphqlannotationsrv.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import fr.an.tests.testgraphqlannotationsrv.domain.Srv4A;
import fr.an.tests.testgraphqlannotationsrv.domain.Srv4B;

@Component
public class ARepository {

	private int aIdGenerator = 1;
	private int bIdGenerator = 1;
	private Map<Integer,Srv4A> aById = new HashMap<>();
	private Map<Integer,Srv4B> bById = new HashMap<>();
	
	public ARepository() {
		for (int i = 1; i < 10; i++) {
			int idA = aIdGenerator++;
			Srv4A ai = new Srv4A(idA, "a" + idA);
			aById.put(ai.id, ai);
			for(int j = 1; j < 10; j++) {
				int bId = bIdGenerator++;
				Srv4B bj = new Srv4B(bId, ai.srv4Name + "-" + "b" + bId);
				bj.setA(ai);
				ai.bs.add(bj);
				bById.put(bj.id, bj);
			}
		}
	}
	
	public List<Srv4A> findAllA() {
		return new ArrayList<>(aById.values());
	}

	public Srv4A findAById(int id) {
		return aById.get(id);
	}

	public List<Srv4B> findAllB() {
		return new ArrayList<>(bById.values());
	}

	public Srv4B findBById(int id) {
		return bById.get(id);
	}

}
