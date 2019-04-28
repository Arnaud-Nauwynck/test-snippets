package fr.an.test.protobuf;

import java.util.List;

import lombok.Data;

@Data
public class UserDTO {

	private String name;
	
	private String email;
	
	private int intValue;
	
	private UserDTO bestFriend; // cycle..
	private List<UserDTO> friends; // cycle..

}
