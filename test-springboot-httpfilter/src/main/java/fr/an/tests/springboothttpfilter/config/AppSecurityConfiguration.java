package fr.an.tests.springboothttpfilter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)  // otherwise @PreAuthorize would not work silently !!
public class AppSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http //
          .authorizeRequests() //
          // .antMatchers("/api/v1/foo/**").permitAll() //
          .anyRequest().permitAll() // 

          .and().httpBasic()
          .and().csrf().disable() // otherwise POST,PUT?? do not work out of the box in curl, need to use X-XSS-Protection headers

          .addFilterAt(new BadlyConsumingRequestInputFilter(), UsernamePasswordAuthenticationFilter.class)
          ; //
    }

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
 
    
}
