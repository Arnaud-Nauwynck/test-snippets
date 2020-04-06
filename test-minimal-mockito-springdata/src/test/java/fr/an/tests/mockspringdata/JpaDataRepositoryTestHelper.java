package fr.an.tests.mockspringdata;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaDataRepositoryTestHelper {

	EntityManagerFactory entityManagerFactory;
	
	public JpaDataRepositoryTestHelper() {
		Map<String,String> props = new HashMap<>();
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("fr.an.test-mockito-springdata", props);
		} catch(Exception ex) {
			throw new RuntimeException("Failed createEntityManagerFactory", ex);
		}
	}
	
	public EntityManager createEM() {
		return entityManagerFactory.createEntityManager();
	}

}
