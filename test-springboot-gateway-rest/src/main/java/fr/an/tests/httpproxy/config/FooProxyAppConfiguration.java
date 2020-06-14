package fr.an.tests.httpproxy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class FooProxyAppConfiguration extends WebSecurityConfigurerAdapter {
 
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth)
//      throws Exception {
//        auth
//          .inMemoryAuthentication()
//          .withUser("user").password("{noop}password").roles("USER") //
//            .and() //
//          .withUser("admin").password("{noop}admin").roles("USER", "ADMIN") // 
//          ;
//    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http //
          .authorizeRequests() //

          .antMatchers("/api-proxy/foo/**").permitAll() //

          .and().httpBasic()
          	.and().csrf().disable() // otherwise POST,PUT?? do not work out of the box in curl, need to use X-XSS-Protection headers
          
          ; //
    }
    
}
