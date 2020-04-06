package testspringbootprofile.impl;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"profile1"})
public class BeanProfile1 {

	private static int counter = 0;

	@PostConstruct
	public void init() {
		System.out.println("######## BeanProfile1.init "  + (++counter));
	}
}
