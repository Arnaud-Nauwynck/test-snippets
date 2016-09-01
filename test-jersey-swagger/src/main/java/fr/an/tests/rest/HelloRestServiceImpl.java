package fr.an.tests.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Component;

import fr.an.tests.rest.HelloRestService.Greeting;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path("/v1/hello")
@Consumes("application/json")
@Produces("application/json")
@Api(value="Hello Rest Service")
public class HelloRestServiceImpl 
// implements HelloRestService 
{

//	@Override
	@GET @Path("/greeting")
	@ApiOperation(nickname="greeting", value="say greeting")
	public Greeting greeting() {
		System.out.println("HelloRestServiceImpl.greeting => ok");
		return new Greeting("Hello");
	}
	
}
