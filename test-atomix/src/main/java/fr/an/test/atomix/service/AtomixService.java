package fr.an.test.atomix.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.atomix.core.Atomix;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AtomixService {

	@Autowired
	@Getter
	private Atomix atomix;
	
	@PostConstruct
	public void init() {
		log.info("atomix.start().join()");
		atomix.start().join();
		log.info(".. done atomix.start()");
	}
}
