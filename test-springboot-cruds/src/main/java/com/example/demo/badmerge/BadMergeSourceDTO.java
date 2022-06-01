package com.example.demo.badmerge;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BadMergeSourceDTO {

	public long id;
	
	private BadMergeReferenceIdDTO ref;
	
	private String field1;
	private String field2;
	
}
