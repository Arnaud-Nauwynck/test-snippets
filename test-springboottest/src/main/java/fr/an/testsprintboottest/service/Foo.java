package fr.an.testsprintboottest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Foo {

	@Autowired
	private Bar bar;
	
	@Value("${app.key}")
	private String appKey;
	
	public int foo(int x) {
		System.out.println("foo() ..");
		int res = bar.bar(x);
		System.out.println(".. foo()");
		return res;
	}
	
	public String confAppKey() {
		return appKey;
	}
}
