package com.example.demo.rest;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.badmerge.BadMergeService;
import com.example.demo.dao.TodoRepository;
import com.example.demo.directquery2dto.DirectQuery2DTOService;
import com.example.demo.domain.TodoEntity;

@Component
public class DbDataInitializer {

	@Autowired
	private TodoRepository repository;

	@Autowired
	private BadMergeService badMergeService;
	
	@Autowired
	private DirectQuery2DTOService directQuery2DTOService;
	
	@PostConstruct
	public void createInit() {
		TodoEntity elt1 = new TodoEntity();
		elt1.setLabel("Apprendre Maven");
		repository.save(elt1);
		
		TodoEntity elt2 = new TodoEntity();
		elt2.setLabel("Apprendre Spring-boot");
		repository.save(elt2);
		
		badMergeService.initDb();
		
		directQuery2DTOService.initDb();
	}

}
