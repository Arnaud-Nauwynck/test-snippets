package com.example.demo.service;

import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.TodoRepository;
import com.example.demo.domain.TodoEntity;
import com.example.demo.rest.DtoConverter;
import com.example.demo.rest.TodoDTO;

@Service
@Transactional
public class TodoService {

	@Autowired
	private TodoRepository repository;
	
	@Autowired
	private DtoConverter dtoConverter;
	
	public List<TodoDTO> list() {
		List<TodoEntity> entities = 
				repository.findAll();
		return entity2Dtos(entities);
	}

	public TodoDTO get(int id) {
		TodoEntity entity = repository.getById(id);
		return entity2Dto(entity);
	}
	
	public TodoDTO createTodo(TodoDTO req) {
		TodoEntity res = repository.save(dto2Entity(req));
		return entity2Dto(res);
	}

	public TodoDTO updateTodo(TodoDTO req) {
		TodoEntity entity = repository.getById(req.id);
		entity.setLabel(req.label);
		entity.setPriority(req.priority);
		return entity2Dto(entity);
	}

	public TodoDTO deleteTodo(int id) {
		TodoEntity entity = repository.getById(id);
		repository.delete(entity);
		return entity2Dto(entity);
	}


	protected TodoDTO entity2Dto(TodoEntity src) {
		return dtoConverter.map(src, TodoDTO.class);
	}

	protected List<TodoDTO> entity2Dtos(Collection<TodoEntity> src) {
		return dtoConverter.mapAsList(src, TodoDTO.class);
	}
	
	protected TodoEntity dto2Entity(TodoDTO src) {
		return dtoConverter.map(src, TodoEntity.class);
	}
	
}
