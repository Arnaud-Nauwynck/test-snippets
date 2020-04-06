package fr.an.testhibernatejpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HibernateJpaHelper {

	EntityManagerFactory entityManagerFactory;
	
	public void setUp() {
		Map<String,String> props = new HashMap<>();
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("fr.an.test-hibernate-jpa", props);
		} catch(Exception ex) {
			throw new RuntimeException("Failed createEntityManagerFactory", ex);
		}
	}
	
	public EntityManager createEM() {
		return entityManagerFactory.createEntityManager();
	}
}
