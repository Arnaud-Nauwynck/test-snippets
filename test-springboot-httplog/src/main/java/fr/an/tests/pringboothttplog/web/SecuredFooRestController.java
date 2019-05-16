package fr.an.tests.pringboothttplog.web;

import javax.annotation.security.RolesAllowed;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.an.tests.pringboothttplog.dto.FooResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Dummy testing purpose Rest Controller, using explicit secured mapping
 */
@RestController
@RequestMapping(path="/api/v1/securedfoo")
@Slf4j
public class SecuredFooRestController {

	@RolesAllowed("ROLE_ADMIN")
	@GetMapping("/getFooPerm1")
	public FooResponse getFooAdmin() {
		log.info("getFooAdmin()");
		return new FooResponse("hello", 123);
	}

	@RolesAllowed("ROLE_USER")
	@GetMapping("/getFooUser")
	public FooResponse getFooUser() {
		log.info("getFooUser()");
		return new FooResponse("hello", 123);
	}

//	curl -X GET -u user:password -H "Accept: application/json" -H "Content-Type: aplication/json" http://localhost:8080/api/v1/securedfoo/getFooPreAuthRoleAdmin
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/getFooPreAuthRoleAdmin")
	public FooResponse getFooPreAuthRoleAdmin() {
		log.info("getFooPreAuthRoleAdmin()");
		return new FooResponse("hello", 123);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping("/getFooPreAuthRoleUser")
	public FooResponse getFooPreAuthRoleUser() {
		log.info("getFooPreAuthRoleUser()");
		return new FooResponse("hello", 123);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/getFooAuthenticated")
	public FooResponse getFooAuthenticated() {
		log.info("getFooAuthenticated()");
		return new FooResponse("hello", 123);
	}

	@Secured("ROLE_USER")
	@GetMapping("/getFooSecuredUser")
	public FooResponse getFooSecuredUser() {
		log.info("getFooSecuredUser()");
		return new FooResponse("hello", 123);
	}

}
