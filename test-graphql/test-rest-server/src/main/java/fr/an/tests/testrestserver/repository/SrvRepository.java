package fr.an.tests.testrestserver.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import fr.an.tests.testrestserver.domain.Srv1A;
import fr.an.tests.testrestserver.domain.Srv1B;

@Component
public class SrvRepository {

	private int aIdGenerator = 1;
	private int bIdGenerator = 1;
	private Map<Integer,Srv1A> aById = new HashMap<>();
	private Map<Integer,Srv1B> bById = new HashMap<>();
	
	public SrvRepository() {
		for (int i = 1; i < 10; i++) {
			int aId = aIdGenerator++;
			Srv1A ai = new Srv1A(aId, "a" + aId);
			aById.put(ai.id, ai);
			for(int j = 1; j < 10; j++) {
				int bjId = bIdGenerator++;
				Srv1B bj = new Srv1B(bjId, ai.srv1Name + "-" + "b" + bjId);
				bj.a = ai;
				ai.bs.add(bj);
				bById.put(bj.id, bj);
			}
		}
	}
	
	public List<Srv1A> findAllA() {
		return new ArrayList<>(aById.values());
	}

	public Srv1A findAById(int id) {
		return aById.get(id);
	}

	public List<Srv1B> findAllB() {
		return new ArrayList<>(bById.values());
	}

	public Srv1B findBById(int id) {
		return bById.get(id);
	}

}
