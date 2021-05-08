package fr.an.test.atomix.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.an.test.atomix.dto.FooMeth1RequestDTO;
import fr.an.test.atomix.dto.FooMeth1ResponseDTO;
import fr.an.test.atomix.service.FooDispatchLeaderStatefullService;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path="/api/foo")
@Slf4j
public class FooRestController {

	@Autowired
	private FooDispatchLeaderStatefullService delegate;
	
	@PostMapping("/meth1")
	public FooMeth1ResponseDTO meth1(@RequestBody FooMeth1RequestDTO req) {
		log.info("meth1");
		val res = delegate.meth1(req);
		log.info("meth1 => " + res);
		return res;
	}
}
