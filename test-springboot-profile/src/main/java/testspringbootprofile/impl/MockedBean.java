package testspringbootprofile.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class MockedBean {

	private static int counter = 0;
	private static int counterFoo = 0;
	
	@PostConstruct
	public void init() {
		System.out.println("######## MockedBean.init " + (++counter));
	}
	
	public void foo() {
		System.out.println("######## MockedBean.foo() " + (++counterFoo));
	}
	
}
