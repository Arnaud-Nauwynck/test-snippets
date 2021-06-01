package fr.an.tests.testspringbootactiviti.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Order(101)
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth
          .inMemoryAuthentication()
          .withUser("user")
          .password("password")
          .roles("USER")
          .and()
          .withUser("admin")
          .password("admin")
          .roles("USER", "ADMIN");
    }
	    

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        	.anyRequest().permitAll()  // HACK ...
        	;
    }

//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.parentAuthenticationManager(authenticationManager)
//                .inMemoryAuthentication()
//                .withUser("john").password("123").roles("USER");
//    }
    
}