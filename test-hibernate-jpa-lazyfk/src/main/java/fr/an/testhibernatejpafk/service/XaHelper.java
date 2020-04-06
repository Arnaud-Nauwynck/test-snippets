package fr.an.testhibernatejpafk.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class XaHelper {

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void xanew(Runnable runnable) {
		runnable.run();
	}
	
}
