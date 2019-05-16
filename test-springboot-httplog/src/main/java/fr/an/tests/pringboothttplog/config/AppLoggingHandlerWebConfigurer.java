package fr.an.tests.pringboothttplog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import fr.an.tests.pringboothttplog.logaspects.AppLoggingHandlerInterceptor;
import fr.an.tests.pringboothttplog.logaspects.AppRequestStreamWrapperFilter;

@Configuration
@ConditionalOnProperty(name = "httplogging.enable", havingValue = "true")
public class AppLoggingHandlerWebConfigurer implements WebMvcConfigurer {
 
    @Autowired
    private AppLoggingHandlerInterceptor interceptor;
 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/api/**");
    }
    
    @Bean
    public AppRequestStreamWrapperFilter requestLoggingFilter() {
    	return new AppRequestStreamWrapperFilter();
    }
    
}