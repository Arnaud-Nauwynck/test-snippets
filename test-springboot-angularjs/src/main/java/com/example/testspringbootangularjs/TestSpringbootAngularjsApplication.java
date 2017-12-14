package com.example.testspringbootangularjs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableJpaRepositories
public class TestSpringbootAngularjsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestSpringbootAngularjsApplication.class, args);
	}
}

class TodoDTO {
	public String todoMessage;
	public Long id;

	public TodoDTO() {
	}
//	public TodoDTO(@JsonProperty("todoMessage") String todoMessage) {
//		this.todoMessage = todoMessage;
//	}

	@Override
	public String toString() {
		return "TodoDTO [" + id + " '" + todoMessage + "']";
	}
	
	
}

@Entity
class TodoEntity {
	
	@Id 
	@GeneratedValue
	public Long id;
	
	public String todoMessage;
	
	
	public TodoEntity() {
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTodoMessage() {
		return todoMessage;
	}
	public void setTodoMessage(String todoMessage) {
		this.todoMessage = todoMessage;
	}
	
	
}

interface TodoRepository extends JpaRepository<TodoEntity,Long> {
	
	List<TodoEntity> findAllByTodoMessage(String msg);
	
}

@RestController
@RequestMapping(value = "/api/todo")
@Transactional // should be on service ...delegating
class TodosController {
	
	@Autowired
	TodoRepository repo;
	
	@GetMapping(value = "")
	public List<TodoDTO> list() {
		List<TodoEntity> entities = repo.findAll();
		return entities2dtos(entities);
	}
	
	private List<TodoDTO> entities2dtos(List<TodoEntity> entities) {
		return entities.stream().map(x -> entity2to(x)).collect(Collectors.toList());
	}

	private TodoDTO entity2to(TodoEntity x) {
		TodoDTO res = new TodoDTO();
		res.id = x.getId();
		res.todoMessage = x.getTodoMessage();
		return res;
	}

	@PostMapping(value="") 
	public TodoDTO post(@RequestBody TodoDTO data) {
		System.out.println("http POST /api/todo " + data);
		TodoEntity entity;
		if (data.id != null) {
			entity = repo.findOne(data.id);
		} else {
			entity = new TodoEntity();
			entity = repo.save(entity);
		}
		entity.setTodoMessage(data.todoMessage);
		return entity2to(entity);
	}
	
}