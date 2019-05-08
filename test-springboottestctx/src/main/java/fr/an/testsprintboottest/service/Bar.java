package fr.an.testsprintboottest.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bar {
	
	private static final Logger log = LoggerFactory.getLogger(Bar.class);

	public Bar() {
		log.info("Bar()");
	}
	
	@PostConstruct
	public void init() {
		log.info("Bar.init()");
	}
	
}
