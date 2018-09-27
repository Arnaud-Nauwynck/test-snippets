package testspringbootprofile.impl;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class SimpleBean {

	private static int counter = 0;
	
	@PostConstruct
	public void init() {
		System.out.println("######## SimpleBean.init " + (++counter));
	}
}
