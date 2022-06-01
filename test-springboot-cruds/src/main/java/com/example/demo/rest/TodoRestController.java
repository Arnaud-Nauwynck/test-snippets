package com.example.demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.TodoService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/todo")
@Slf4j
public class TodoRestController {

	@Autowired
	private TodoService service;
	
	@GetMapping()
	public List<TodoDTO> list() {
		List<TodoDTO> res = service.list();
		return res;
	}

	@GetMapping("/{id}")
	public TodoDTO get(@PathVariable("id") int id) {
		return service.get(id);
	}
	
	@PostMapping
	public ResponseDTO postTodo(
			@RequestBody TodoDTO req) {
		// step 1/3: unmarshall, check inputs, convert, logs...
		long start = System.currentTimeMillis();
		log.info("http POST /api/todo");

		// step 2/3: delegate to service
		TodoDTO res = service.createTodo(req);
		
		// step 3/3: convert, format output, logs...
		log.info(".. done http POST, took " + (System.currentTimeMillis()-start) + " ms");
		return new ResponseDTO(res.id, res.label);
	}
	
	@PutMapping
	public TodoDTO putTodo(@RequestBody TodoDTO req) {
		log.info("http PUT /api/todo");
		TodoDTO res = service.updateTodo(req);
		return res;
	}

	@DeleteMapping("/{id}")
	public TodoDTO deleteTodo(@PathVariable("id") int id) {
		log.info("http DELETE /api/todo");
		TodoDTO res = service.deleteTodo(id);
		return res;
	}

}
