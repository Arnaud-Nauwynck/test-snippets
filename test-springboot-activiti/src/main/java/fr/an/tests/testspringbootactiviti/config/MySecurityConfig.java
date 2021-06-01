package fr.an.tests.testspringbootactiviti.config;

// @Configuration
// cf ActivitySecurityConfig replacement !!!!
public class MySecurityConfig {


	// in spring-boot version >= 2.*
//    @Bean
//	public UserDetailsService userDetailsService() {
//		UserDetails user = User.builder()
//				.username("user")
//				.password("password")
//				.roles("USER")
//				.build();
//
//		return new InMemoryUserDetailsManager(user);
//	}


	// cf InMemoryUserDetailsManagerConfigurer => application  security.*
	
//		auth.inMemoryAuthentication().passwordEncoder(NoOpPasswordEncoder.getInstance())
//		    .withUser("admin").password("admin1pass").roles("USER", "ADMIN").and()
//		    .withUser("user1").password("user1pass").roles("USER").and()
//		    .withUser("user2").password("user2pass").roles("USER").and()
//		    .withUser("user3").password("user3pass").roles("USER");
//	}


}
