package fr.an.tests.springbootswagger.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.an.tests.springbootswagger.dto.EnumStatus;
import fr.an.tests.springbootswagger.dto.FooResponse;
import lombok.extern.slf4j.Slf4j;

// cf ignore in swagger because of PathSelectors.ant("/api/**")
@RestController
@RequestMapping(path="/other/v1/foo")
// @OpenAPIDefinition(tags = "unused-because-/other-not-match-PathSelectors('/api/**')")
@Slf4j
public class FooOtherController {

	@GetMapping("/getFoo")
	public FooResponse getFoo() {
		log.info("getFoo()");
		return new FooResponse("hello", 123, EnumStatus.OK);
	}
	
}
