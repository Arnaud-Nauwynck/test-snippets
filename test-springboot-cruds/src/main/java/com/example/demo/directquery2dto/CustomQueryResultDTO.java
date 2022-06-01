package com.example.demo.directquery2dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomQueryResultDTO {

	public long id; 
	public String field1;
	public String field2;
	
	public long refId; 
	public String refField1;
	public String refField2;

}
