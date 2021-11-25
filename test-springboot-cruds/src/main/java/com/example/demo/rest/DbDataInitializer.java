package com.example.demo.rest;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbDataInitializer {

	@Autowired
	private TodoRepository repository;

	@PostConstruct
	public void createInit() {
		TodoEntity elt1 = new TodoEntity();
		elt1.label = "Apprendre Maven";
		repository.save(elt1);
		
		TodoEntity elt2 = new TodoEntity();
		elt2.label = "Apprendre Spring-boot";
		repository.save(elt2);
	}

}
