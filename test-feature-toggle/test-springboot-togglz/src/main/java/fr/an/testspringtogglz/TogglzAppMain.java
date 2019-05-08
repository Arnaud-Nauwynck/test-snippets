package fr.an.testspringtogglz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.togglz.core.Feature;
import org.togglz.core.activation.UsernameActivationStrategy;
import org.togglz.core.annotation.ActivationParameter;
import org.togglz.core.annotation.DefaultActivationStrategy;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.util.NamedFeature;
import org.togglz.spring.web.FeaturesAreActive;

@SpringBootApplication
public class TogglzAppMain {

	public static void main(String[] args) {
		SpringApplication.run(TogglzAppMain.class, args);
	}
	
}


// .. cf TogglzAutoConfiguration for defaults
//@Configuration
//@ConditionalOnProperty(name="togglzDb.enable", matchIfMissing=false)
//class Config {
//	
//	@Autowired 
//	private DataSource dataSource;
//	
//	@Bean
//	public StateRepository stateRepository() {
//		return JDBCStateRepository.newBuilder(dataSource)
//			      .tableName("features")
//			      .createTable(false)
//			      .serializer(DefaultMapSerializer.singleline())
//			      .noCommit(true)
//			      .build();
//	}
//	
//}

enum AppFeatures implements Feature {

	@DefaultActivationStrategy(id = UsernameActivationStrategy.ID,
		 parameters = { @ActivationParameter(name = UsernameActivationStrategy.PARAM_USERS, value = "user1")}
	)
	Feature3;
}

enum AppOtherFeatures implements Feature {

	Feature4;
}

enum WebVisibleFeatures implements Feature {

	Feature5;
}

@RestController
@RequestMapping(path = "/api/v1")
class HelloController {

	@Autowired
	private FeatureManager manager;
	
	public static final Feature Feature1 = new NamedFeature("Feature1");
	public static final Feature Feature2 = new NamedFeature("Feature2");
	
	@GetMapping("/helloFeature1")
	public String helloFeature1() {
        if (manager.isActive(Feature1)) {
        	return "Hello Feature1";
        }
    	return "Hello";
	}

	@GetMapping("/helloFeature2")
	public String helloFeature2() {
        if (manager.isActive(log)) {
        	return "Hello Feature2";
        }
    	return "Hello";
	}

	@GetMapping("/helloFeature3")
	public String helloFeature3() {
        if (manager.isActive(AppFeatures.Feature3)) {
        	return "Hello Feature3";
        }
        return "Hello";
	}
	
	@GetMapping("/helloFeature4")
	public String helloFeature4() {
        if (manager.isActive(AppOtherFeatures.Feature4)) {
        	return "Hello Feature4";
        }
    	return "Hello";
	}

	// cf togglz.web.register-feature-interceptor: true
	// => called at runtime, to return http code 409
	@FeaturesAreActive(featureClass=WebVisibleFeatures.class, features="Feature5", responseStatus=409)
	@GetMapping(value="/helloFeature5")
	public String newSecureFeature() {
		if (! manager.isActive(WebVisibleFeatures.Feature5)) {
			throw new IllegalStateException("should not be called");
		}
		return "Hello Feature5";
	}

}
