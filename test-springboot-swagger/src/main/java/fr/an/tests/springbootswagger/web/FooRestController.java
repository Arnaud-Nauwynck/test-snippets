package fr.an.tests.springbootswagger.web;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.an.tests.springbootswagger.dto.FooRequest;
import fr.an.tests.springbootswagger.dto.FooResponse;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;


/**
 * Dummy testing purpose Rest Controller
 */
@RestController
@RequestMapping(path="/api/v1/foo")
@Api(tags = "foo")
@Slf4j
public class FooRestController {

	@GetMapping("/getFoo")
	public FooResponse getFoo() {
		log.info("getFoo()");
		return new FooResponse("hello", 123);
	}

	@GetMapping("/getFoos")
	public List<FooResponse> getFoos() {
		log.info("getFoos()");
		return Arrays.asList(new FooResponse("hello", 123), new FooResponse("world", 234));
	}

	@PostMapping("/postFoo")
	public FooResponse postFoo(@RequestBody FooRequest req) {
		log.info("postFoo()");
		return new FooResponse(req.strValue, req.intValue);
	}

	@PutMapping("/putFoo")
	public FooResponse putFoo(@RequestBody FooRequest req) {
		log.info("putFoo()");
		return new FooResponse(req.strValue, req.intValue);
	}

	@GetMapping(path = "/getText", produces = "plain/text")
	public String getText() {
		log.info("getText()");
		return "hello";
	}

}
