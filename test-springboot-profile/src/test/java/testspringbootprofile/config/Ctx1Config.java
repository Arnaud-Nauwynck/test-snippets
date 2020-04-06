package testspringbootprofile.config;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@EnableAutoConfiguration
@ComponentScan("testspringbootprofile")
@ActiveProfiles({"otherctx"})
public class Ctx1Config {

	@PostConstruct
	public void init() {
		System.out.println("Ctx1Config.init");
	}
}
