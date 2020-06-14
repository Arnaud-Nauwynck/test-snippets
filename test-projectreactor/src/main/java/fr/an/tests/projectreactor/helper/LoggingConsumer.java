package fr.an.tests.projectreactor.helper;

import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingConsumer<T> implements Consumer<T> {
	
	private final String name;
	
	public LoggingConsumer(String name) {
		this.name = name;
	}

	@Override
	public void accept(T t) {
		log.info("##  " + name + " accept " + t);
	}
	
}