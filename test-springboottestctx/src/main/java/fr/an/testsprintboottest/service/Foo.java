package fr.an.testsprintboottest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Foo {
	
	@Value("${app.key}")
	private String appKey;
	
	@Autowired 
	Bar bar;
	
	public int foo(int x) {
		System.out.println("foo() ..");
		return x + 1;
	}
	
	public String confAppKey() {
		return appKey;
	}
}
