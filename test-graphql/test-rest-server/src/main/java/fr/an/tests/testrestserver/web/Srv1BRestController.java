package fr.an.tests.testrestserver.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.an.tests.testrestserver.domain.Srv1B;
import fr.an.tests.testrestserver.repository.SrvRepository;

@RestController
@RequestMapping(path="/rest/b")
public class Srv1BRestController {

	@Autowired
	private SrvRepository repo;
	
	@GetMapping(path="")
	public List<Srv1B> all() {
		return repo.findAllB();
	}
	
	@GetMapping(path="/{id}")
	public Srv1B byId(@PathVariable("id") int id) {
		return repo.findBById(id);
	}
	
}
