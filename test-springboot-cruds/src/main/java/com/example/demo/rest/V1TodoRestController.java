package com.example.demo.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/todo")
public class V1TodoRestController {

	private List<TodoDTO> entities = createInit();

	private static int idGenerator = 0;
	
	public List<TodoDTO> createInit() {
		List<TodoDTO> res = new ArrayList<TodoDTO>();
		
		TodoDTO elt1 = new TodoDTO();
		elt1.id = ++idGenerator;
		elt1.label = "Apprendre Maven";
		res.add(elt1);
		
		TodoDTO elt2 = new TodoDTO();
		elt2.id = ++idGenerator;
		elt2.label = "Apprendre Spring-boot";
		res.add(elt2);

		return res;
	}
	
	@GetMapping()
	public List<TodoDTO> list() {
		return entities;
	}

	@PostMapping
	public TodoDTO postTodo(@RequestBody TodoDTO req) {
		System.out.println("called http POST /api/todo");
		req.id = ++idGenerator;
		entities.add(req);
		return req;
	}

	@PutMapping
	public TodoDTO putTodo(@RequestBody TodoDTO req) {
		System.out.println("called http PUT /api/todo");
		TodoDTO entity = getById(req.id);
		entity.label = req.label;
		entity.priority = req.priority;
		return entity;
	}

	@DeleteMapping("/{id}")
	public TodoDTO deleteTodo(@PathVariable("id") int id) {
		System.out.println("called http DELETE /api/todo");
		TodoDTO entity = getById(id);
		entities.remove(entity);
		return entity;
	}

	@GetMapping("/{id}")
	public TodoDTO get(@PathVariable("id") int id) {
		TodoDTO entity = getById(id);
		return entity;
	}

	private TodoDTO getById(int id) {
		TodoDTO res = findById(id);
		if (res == null) {
			throw new NoSuchElementException();
		}
		return res;
	}
	private TodoDTO findById(int id) {
		// return entities.stream().findFirst(e -> e.id == id).orElse(null);
		for(TodoDTO elt : entities) {
			if (elt.id == id) {
				return elt;
			}
		}
		return null;
	}
	
}
