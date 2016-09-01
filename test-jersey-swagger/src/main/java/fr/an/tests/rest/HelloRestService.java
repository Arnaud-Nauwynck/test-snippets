package fr.an.tests.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import fr.an.tests.rest.HelloRestService.Greeting;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

// @Path("/v1/hello")
@Consumes("application/json")
@Produces("application/json")

@Api(value="Hello Rest Service")
public interface HelloRestService {


	public static class Greeting {
		public String msg;

		public Greeting() {
		}
		public Greeting(String msg) {
			this.msg = msg;
		}
		
	}
	
	@GET @Path("/greeting")
	@ApiOperation(nickname="greeting", value="say greeting")
	@ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Greeting.class)
    })
	public Greeting greeting();
	
}
