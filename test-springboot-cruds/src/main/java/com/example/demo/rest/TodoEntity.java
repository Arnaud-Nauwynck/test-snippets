package com.example.demo.rest;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

/**
 * DTO = Data Transfer Object
 */
@Entity
@Data
public class TodoEntity {
	@Id @GeneratedValue
	private int id;
	
	String label;
	private String creationDate;
	int priority;
	
}