package com.example.demo.rest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// @RequestMapping("/api/v5/todo")
@RepositoryRestResource(
		collectionResourceRel = "todos",
		path = "todo"
		//?? collectionResourceDescription = @Description()
		)
public interface V5TodoRepositoryRestResource 
	extends // JpaRepository<TodoEntity, Integer> // ... does not work?
		PagingAndSortingRepository<TodoEntity, Integer>   // <= by default, use page of size 20 .. this is never a good default (too small)
		{
}

