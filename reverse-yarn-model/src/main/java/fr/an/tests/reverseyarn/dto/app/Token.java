package fr.an.tests.reverseyarn.dto.app;

import java.nio.ByteBuffer;

import lombok.Data;

@Data
public class Token {
	
	public ByteBuffer identifier;
	public ByteBuffer password;
	public String kind;
	public String service;

}
