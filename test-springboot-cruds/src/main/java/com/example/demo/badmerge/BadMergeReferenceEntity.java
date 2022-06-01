package com.example.demo.badmerge;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class BadMergeReferenceEntity {

	@Id
	@GeneratedValue
	private long id;

	private String displayName;
	
	private String field1;
	private String field2;
	private String field3;
	private String field4;
	private String field5;

}
