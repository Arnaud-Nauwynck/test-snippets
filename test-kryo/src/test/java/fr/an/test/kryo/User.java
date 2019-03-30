package fr.an.test.kryo;

import lombok.Data;

@Data
public class User {

	private String name;
	public String pubFirstName;
	
	private int intValue;
	
	private User friend; // cycle..

}
