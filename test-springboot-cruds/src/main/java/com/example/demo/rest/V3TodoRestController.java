package com.example.demo.rest;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

import com.example.demo.dao.TodoRepository;
import com.example.demo.domain.TodoEntity;

@RestController
@RequestMapping("/api/v3/todo")
@Transactional
public class V3TodoRestController {

	@Autowired
	private TodoRepository repository;
	
	@GetMapping()
	public List<TodoDTO> list() {
		return entity2Dtos(repository.findAll());
	}

	@PostMapping
	public TodoDTO postTodo(@RequestBody TodoDTO req) {
		System.out.println("called http POST /api/todo");
		TodoEntity res = repository.save(dto2Entity(req));
		return entity2Dto(res);
	}

	@PutMapping
	public TodoDTO putTodo(@RequestBody TodoDTO req) {
		System.out.println("called http PUT /api/todo");
		TodoEntity entity = repository.getById(req.id);
		entity.setLabel(req.label);
		entity.setPriority(req.priority);
		return entity2Dto(entity);
	}

	@DeleteMapping("/{id}")
	public TodoDTO deleteTodo(@PathVariable("id") int id) {
		System.out.println("called http DELETE /api/todo");
		TodoEntity entity = repository.getById(id);
		repository.delete(entity);
		return entity2Dto(entity);
	}

	@GetMapping("/{id}")
	public TodoDTO get(@PathVariable("id") int id) {
		TodoEntity entity = repository.getById(id);
		return entity2Dto(entity);
	}

	public TodoDTO entity2Dto(TodoEntity src) {
		TodoDTO res = new TodoDTO();
		res.id = src.getId();
		res.label = src.getLabel();
		res.priority = src.getPriority();
		// other fields... 
		return res;
	}

	public TodoEntity dto2Entity(TodoDTO src) {
		TodoEntity res = new TodoEntity();
		res.setLabel(src.label);
		res.setPriority(src.priority);
		// other fields... 
		return res;
	}

	public List<TodoDTO> entity2Dtos(Collection<TodoEntity> src) {
		return src.stream().map(e -> entity2Dto(e)).collect(Collectors.toList());
	}
	
}
