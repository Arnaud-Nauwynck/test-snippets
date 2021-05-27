package fr.an.tests.springbootswagger.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.an.tests.springbootswagger.dto.FooResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;


/**
 * Dummy testing purpose Rest Controller
 */
@RestController
@RequestMapping(path="/api/v1/fooNoSwagger")
@Hidden // cf @ApiIgnore springfox.documentation.spi.service.contexts.ApiSelector
@Slf4j
public class FooNoSwaggerRestController {

	@GetMapping("/getFoo")
	public FooResponse getFoo() {
		log.info("getFoo()");
		return new FooResponse("hello", 123);
	}

}
