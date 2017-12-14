package com.example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/hello")
public class HelloRestController {

	public static class Greeting {
		public String msg;
		public Greeting(String msg) { this.msg = msg; }		
	}
	
	@RequestMapping(path="/greeting")
	public Greeting greetingTo(
			@RequestParam(name="user", required=false) String user) {
		return new Greeting("Hello " + ((user != null)? user : "springboot user"));
	}

	@RequestMapping(path="/greetingText")
	public String greetingTextTo(
			@RequestParam(name="user", required=false) String user) {
		return "Hello " + ((user != null)? user : "springboot user");
	}

}
