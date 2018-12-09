package com.example.demo;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public MapperFacade mapperFacade() {
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder()
				  .mapNulls(false).build();
		return mapperFactory.getMapperFacade();
	}
}

interface TodoRepository extends JpaRepository<Todo,Integer> {
	
}

@RestController
@RequestMapping(path="/api/todo"
		// produces="application/json", consumes="application/json"
		)
@Transactional
class TodoRestController {
	@Autowired private TodoRepository repo;
	@Autowired private MapperFacade mapperFacade;
	
	@PostConstruct
	public void init() {
		for(int i = 1; i < 1_000; i++) {
			Todo e = new Todo();
			e.setTask("learn springboot " + i);
			e.setComment(".. " + i);
			e.setDate(new Date());
			repo.save(e);
		}
	}
	
	@PostMapping
	public TodoDTO create(@RequestBody TodoDTO src) {
		Todo entity = new Todo();
		mapperFacade.map(src, entity);
		entity.setId(0);
		
		repo.save(entity);
		
		return mapperFacade.map(entity, TodoDTO.class);
	}
	
	@GetMapping
	public List<TodoDTO> list() {
		List<Todo> tmpres = repo.findAll();
		return mapperFacade.mapAsList(tmpres, TodoDTO.class);
	}
	
	@PutMapping
	public TodoDTO update(@RequestBody TodoDTO src) {
		Todo entity = repo.findById(src.getId()).orElseThrow(() -> new RuntimeException("not found"));
		mapperFacade.map(src, entity);
		return mapperFacade.map(entity, TodoDTO.class);
	}

}
