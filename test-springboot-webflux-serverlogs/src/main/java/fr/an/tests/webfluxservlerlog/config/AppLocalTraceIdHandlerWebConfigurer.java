package fr.an.tests.webfluxservlerlog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import fr.an.tests.webfluxservlerlog.ws.AppLocalTraceIdHandlerInterceptor;

@Configuration
public class AppLocalTraceIdHandlerWebConfigurer implements WebMvcConfigurer {
 
    @Autowired
    private AppLocalTraceIdHandlerInterceptor interceptor;
 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/api/**");
    }
    
}