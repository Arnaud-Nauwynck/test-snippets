package com.example.demo.rest;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/todo")
@Transactional
public class V2TodoRestController {
	
	@Autowired
	private TodoRepository repository;

	@GetMapping()
	public List<TodoEntity> list() {
		return repository.findAll();
	}

	@PostMapping
	public TodoEntity postTodo(@RequestBody TodoEntity req) {
		System.out.println("called http POST /api/todo");
		TodoEntity res = repository.save(req);
		return res;
	}

	@PutMapping
	public TodoEntity putTodo(@RequestBody TodoEntity req) {
		System.out.println("called http PUT /api/todo");
		TodoEntity entity = repository.getById(req.getId());
		entity.setLabel(req.label);
		entity.setPriority(req.priority);
		return entity;
	}

	@DeleteMapping("/{id}")
	public TodoEntity deleteTodo(@PathVariable("id") int id) {
		System.out.println("called http DELETE /api/todo");
		TodoEntity entity = repository.getById(id);
		repository.delete(entity);
		return entity;
	}

	@GetMapping("/{id}")
	public TodoEntity get(@PathVariable("id") int id) {
		TodoEntity entity = repository.getById(id);
		return entity;
	}

}
