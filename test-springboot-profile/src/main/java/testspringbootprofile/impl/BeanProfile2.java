package testspringbootprofile.impl;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"profile2"})
public class BeanProfile2 {

	private static int counter = 0;

	@PostConstruct
	public void init() {
		System.out.println("######## BeanProfile2.init " + (++counter));
	}
}
