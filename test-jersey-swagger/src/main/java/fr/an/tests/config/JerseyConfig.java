package fr.an.tests.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.wadl.internal.WadlResource;
import org.springframework.context.annotation.Configuration;

import fr.an.tests.rest.HelloRestServiceImpl;
import fr.an.tests.rest.HelloRestServiceImpl2;

@Configuration
@ApplicationPath("/rest") // otherwise no more static resources....
public class JerseyConfig 
extends ResourceConfig 
{

	public JerseyConfig() {
		register(HelloRestServiceImpl.class); // see http://localhost:8080/rest/v1/hello/greeting
		register(HelloRestServiceImpl2.class); // see http://localhost:8080/rest/v1/hello2/greeting
		
		register(WadlResource.class); // see http://localhost:8080/rest/application.wadl
		
		register(io.swagger.jaxrs.listing.ApiListingResource.class); // see   http://localhost:8080/rest/swagger.json
		register(io.swagger.jaxrs.listing.SwaggerSerializers.class);          
        // register(CrossDomainFilter.class);  // ??
        register(org.glassfish.jersey.jackson.JacksonFeature.class);
        
	}
}
