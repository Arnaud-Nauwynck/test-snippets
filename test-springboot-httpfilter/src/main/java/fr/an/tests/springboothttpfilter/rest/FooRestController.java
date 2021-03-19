package fr.an.tests.springboothttpfilter.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.an.tests.springboothttpfilter.dto.FooRequest;
import fr.an.tests.springboothttpfilter.dto.FooResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Dummy testing purpose Rest Controller
 */
@RestController
@RequestMapping(path="/api/v1/foo")
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
		log.info("postFoo() req:" + req);
		return new FooResponse(req.strValue, req.intValue);
	}

	@PutMapping("/putFoo")
	public FooResponse putFoo(@RequestBody FooRequest req) {
		log.info("putFoo() req:" + req);
		return new FooResponse(req.strValue, req.intValue);
	}

	@PostMapping("/postFooStream")
	public FooResponse postFoo(@RequestBody InputStream bodyStream) throws IOException {
		log.info("postFoo() bodyStream..");
		StringBuilder body = new StringBuilder();
		
		for(;;) {
			int b = bodyStream.read();
			if (b == -1) {
				break;
			}
			char ch = (char) b;
			body.append(ch);
		}
		
		return new FooResponse(body.toString(), 0);
	}

}
