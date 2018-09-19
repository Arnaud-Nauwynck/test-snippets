package fr.an.tests.testrestserver.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.an.tests.testrestserver.domain.Srv1A;
import fr.an.tests.testrestserver.repository.SrvRepository;

@RestController
@RequestMapping(path="/rest/a")
public class Srv1ARestController {

	@Autowired
	private SrvRepository repo;
	
	@GetMapping(path="")
	public List<Srv1A> all() {
		return repo.findAllA();
	}
	
	@GetMapping(path="/{id}")
	public Srv1A byId(@PathVariable("id") int id) {
		return repo.findAById(id);
	}
}
