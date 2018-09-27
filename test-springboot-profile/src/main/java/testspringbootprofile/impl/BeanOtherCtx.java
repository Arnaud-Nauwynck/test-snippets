package testspringbootprofile.impl;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"otherctx"})
public class BeanOtherCtx {

	private static int counter = 0;

	@PostConstruct
	public void init() {
		System.out.println("######## BeanOtherCtx.init "  + (++counter));
	}
}
