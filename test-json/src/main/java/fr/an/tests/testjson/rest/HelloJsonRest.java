package fr.an.tests.testjson.rest;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

@Service
@Path("/hello") // => "/cxf/hello"
@Produces({"application/json"})
@Consumes({"application/json"})
public class HelloJsonRest {

    @Path("/helloString")
    @GET
    public String home() {
        return "Hello World!";
    }

    @Path("/helloArray")
    @GET
    public String[] homeArray() {
        return new String[] { "Hello", "World!" };
    }

    @Path("/helloMap")
    @GET
    public Map<String,String> homeMap() {
        return ImmutableMap.of("en", "Hello World");
    }

    @Path("/helloObj")
    @GET
    public Salutation homeObj() {
        return new Salutation("Hello");
    }

}
