package fr.an.tests.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Component;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Component
@Path("/v1/hello2")
@Consumes("application/json")
@Produces("application/json")   // does not work if removed!!!  (redundant with implements?)
public class HelloRestServiceImpl2 implements HelloRestService {

	@Override
	@ApiOperation(nickname="greeting", value="say greeting") // redundant??
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Greeting.class)
    })
	public Greeting greeting() {
		System.out.println("HelloRestServiceImpl2.greeting => ok");
		return new Greeting("Hello");
	}
	
}
