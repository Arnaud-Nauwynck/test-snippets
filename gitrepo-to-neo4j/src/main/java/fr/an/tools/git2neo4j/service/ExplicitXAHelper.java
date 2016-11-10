package fr.an.tools.git2neo4j.service;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ExplicitXAHelper {

	@Transactional
	public void doInXA(Runnable runnable) {
		runnable.run();
	}

	@Transactional
	public <T> T doInXA(Callable<T> callable) {
		try {
			return callable.call();
		} catch(RuntimeException ex) {
			throw ex;
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
