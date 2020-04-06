package fr.an.tests.pringboothttplog.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.an.tests.pringboothttplog.dto.FooResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Dummy testing purpose Rest Controller, explicitely allowing anonymous (path matching '/api/v1/anonymous/**' )
 */
@RestController
@RequestMapping(path="/api/v1/anonymous/foo")
@Slf4j
public class AnonymousFooRestController {

	@GetMapping("/getFoo")
	public FooResponse getFoo() {
		log.info("getFoo()");
		return new FooResponse("hello", 123);
	}

}
