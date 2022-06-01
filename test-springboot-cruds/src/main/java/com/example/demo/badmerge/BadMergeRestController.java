package com.example.demo.badmerge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bad-merge")
public class BadMergeRestController {

	@Autowired
	private BadMergeService service;

	@PostMapping("/create-bug")
	public BadMergeSourceDTO createBug(@RequestBody BadMergeSourceDTO req) {
		return service.createBUG(req);
	}

	@PostMapping("/create-ok")
	public BadMergeSourceDTO createOk(@RequestBody BadMergeSourceDTO req) {
		return service.createOk(req);
	}

	
	@GetMapping("/{id}")
	public BadMergeSourceDTO get(@PathVariable("id") long id) {
		BadMergeSourceDTO res = service.get(id);
		return res;
	}
	
	@PutMapping()
	public BadMergeSourceDTO update(@RequestBody BadMergeSourceDTO req) {
		BadMergeSourceDTO res = service.update(req);
		return res;
	}
	
	
	
	@GetMapping("/ref/{id}")
	public BadMergeReferenceDTO getRef(@PathVariable("id") long id) {
		BadMergeReferenceDTO res = service.getRef(id);
		return res;
	}
	
}
