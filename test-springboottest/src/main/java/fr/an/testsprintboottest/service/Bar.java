package fr.an.testsprintboottest.service;

import org.springframework.stereotype.Component;

@Component
public class Bar {

	public int bar(int x) {
		System.out.println("bar()");
		return x + 1;
	}
}
