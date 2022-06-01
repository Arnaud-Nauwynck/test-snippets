package com.example.demo.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter @Setter
public class TodoEntity {
	@Id @GeneratedValue
	private int id;
	
	private String label;
	private String creationDate;
	private int priority;
	
}