package fr.an.tests.testspringbootactiviti.config;

import javax.annotation.PostConstruct;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivitySecurityConfig {

	@Autowired
	IdentityService identityService;
	
	@PostConstruct
    public void init() {
		Group group = identityService.newGroup("user");
        group.setName("users");
        group.setType("security-role");
        identityService.saveGroup(group);

        User admin = identityService.newUser("admin");
        admin.setPassword("admin");
        identityService.saveUser(admin);
	}
	
//	@Bean
//    public InitializingBean usersAndGroupsInitializer(IdentityService identityService) {
//
//        return new InitializingBean() {
//            public void afterPropertiesSet() throws Exception {
//                
//            }
//        };
//    }
	 
}
