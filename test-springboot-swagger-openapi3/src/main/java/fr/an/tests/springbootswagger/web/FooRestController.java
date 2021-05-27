package fr.an.tests.springbootswagger.web;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.an.tests.springbootswagger.dto.FooRequest;
import fr.an.tests.springbootswagger.dto.FooResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import lombok.extern.slf4j.Slf4j;


/**
 * Dummy testing purpose Rest Controller
 */
@RestController
@RequestMapping(path="/api/v1/foo")
@OpenAPIDefinition(
		//tags = { Tag("foo") }
		)
@Slf4j
public class FooRestController {

	private static final String HELLO_TEXT_TO_ESCAPE = "hello with quote \", ', \\, \\\" ..";
	
	@Autowired
	private ObjectMapper objectMapper;
	
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
		return HELLO_TEXT_TO_ESCAPE;
	}

//	similar to getText()
//	@GetMapping(path = "/getText2", produces = "plain/text")
//	public ResponseEntity<String> getText2() {
//		log.info("getText()");
//		return ResponseEntity.ok(HELLO_TEXT_TO_ESCAPE);
//	}
//
//	similar to getText()
//	@GetMapping(path = "/getText3")
//	public ResponseEntity<String> getText3() {
//		log.info("getText3()");
//		return ResponseEntity.ok(HELLO_TEXT_TO_ESCAPE);
//	}

	@GetMapping(path = "/getJsonText", produces = "application/json")
	public ResponseEntity<String> getJsonText() throws JsonProcessingException {
		log.info("getJsonText()");
		return ResponseEntity.ok(objectMapper.writeValueAsString(HELLO_TEXT_TO_ESCAPE));
	}

// Error ... not wrapped as json!
//	@GetMapping(path = "/getJsonText2", produces = "application/json")
//	public ResponseEntity<String> getJsonText2() {
//		log.info("getJsonText2()");
//		return ResponseEntity.ok(HELLO_TEXT_TO_ESCAPE);
//	}
//
// Error ... not wrapped as json!
//	@GetMapping(path = "/getJsonText3", produces = "application/json")
//	public String getJsonText3() {
//		log.info("getJsonText()");
//		return HELLO_TEXT_TO_ESCAPE;
//	}

	@GetMapping(path = "/getJsonCharArray", produces = "application/json")
	public char[] getJsonCharArray() throws JsonProcessingException {
		log.info("getJsonCharArray()");
		return objectMapper.writeValueAsString(HELLO_TEXT_TO_ESCAPE).toCharArray();
	}

	@GetMapping("/getFooSlow")
	public FooResponse getFooSlow() {
		log.info("getFooSlow()");
		sleep(10000);
		return new FooResponse("hello", 123);
	}

	@GetMapping("/getFoo401")
	public ResponseEntity<FooResponse> getFoo4xx() {
		log.info("getFoo4xx()");
		return ResponseEntity.status(401).build(); // body(new FooResponse("hello", 123));
	}

	@GetMapping("/getFooFailed5xx")
	public FooResponse getFoo5xx() {
		log.info("getFoo5xx()");
		throw new RuntimeException("Failing message", new Exception("Failing cause"));
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

}
