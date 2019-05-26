package fr.an.tests.springbootswagger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class AppHttpConfiguration extends WebSecurityConfigurerAdapter {
 
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth)
//      throws Exception {
//    	// java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"
//    	// .. cf https://www.mkyong.com/spring-boot/spring-security-there-is-no-passwordencoder-mapped-for-the-id-null/
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

          .antMatchers("/api/v1/foo/**").permitAll() //

//          .antMatchers("/api/v1/permitall/**").permitAll() //
//          .antMatchers("/api/v1/anonymous/**").anonymous() //
//     	  .antMatchers("/api/v1/admin/**").hasRole("ADMIN") //
//     	  .antMatchers("/api/v1/secureduser/**").hasRole("USER") //
//          .anyRequest().authenticated() // 

          .and().httpBasic()
          	.and().csrf().disable() // otherwise POST,PUT?? do not work out of the box in curl, need to use X-XSS-Protection headers
          
          ; //
    }
    
}
