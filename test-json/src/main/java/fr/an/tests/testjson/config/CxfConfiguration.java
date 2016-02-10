package fr.an.tests.testjson.config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.spring.SpringResourceFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Configuration
@ImportResource({"classpath:META-INF/cxf/cxf.xml", "classpath:META-INF/cxf/cxf-servlet.xml"})
public class CxfConfiguration {
    
    private static final Logger LOG = LoggerFactory.getLogger(CxfConfiguration.class);

    @Bean
    public ServletRegistrationBean servletRegistrationBean(ApplicationContext context) {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new CXFServlet(), "/cxf/*");
        registrationBean.setAsyncSupported(true);
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }
    
    @Autowired
    private ApplicationContext ctx;

    @Value("${cxf.path:/cxf/*}")
    private String cxfPath;

    @Value("${cxf.log.requests:false}")
    private boolean logRequests;
    
    
    @Bean
    public Server jaxRsServer() {
        // Find all beans annotated with @Path
        String[] rsBeanNames = ctx.getBeanNamesForAnnotation(Path.class);
        if (rsBeanNames == null || rsBeanNames.length == 0) {
            return null;
        }

        StringBuilder displayResourceInfos = new StringBuilder();
        List<ResourceProvider> resourceProviders = new LinkedList<ResourceProvider>();
        for (String beanName : rsBeanNames) {
            SpringResourceFactory resource = new SpringResourceFactory(beanName);
            resource.setApplicationContext(ctx);
            resourceProviders.add(resource);
            Path annot = ctx.findAnnotationOnBean(beanName, Path.class);
            String resourceInfo = beanName + ((annot != null)? ":@Path(\"" + annot.value() + "\")": "") + ", ";
            displayResourceInfos.append(resourceInfo);
        }
        LOG.info("Registering service beans: " + displayResourceInfos);

        // Find all beans annotated with @Providers
        List<Object> providers = new ArrayList<Object>(ctx.getBeansWithAnnotation(Provider.class).values());
        LOG.info("Registering providers: " + providers);

        JAXRSServerFactoryBean rsServerFactory = new JAXRSServerFactoryBean();
        rsServerFactory.setBus(ctx.getBean(SpringBus.class));
        // rsServerFactory.setAddress("/cxf/");
        rsServerFactory.setResourceProviders(resourceProviders);
        rsServerFactory.setProviders(providers);
        Server server = rsServerFactory.create();

        if (logRequests) {
            server.getEndpoint().getInInterceptors().add(new LoggingInInterceptor());
        }
        
        return server;
    }

    @Bean
    @ConditionalOnMissingBean
    public JacksonJsonProvider jsonProvider(ObjectMapper objectMapper) {
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(objectMapper);
        return provider;
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
}
