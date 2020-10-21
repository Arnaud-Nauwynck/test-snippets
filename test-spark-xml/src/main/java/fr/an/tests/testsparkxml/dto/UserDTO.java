package fr.an.tests.testsparkxml.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO implements Serializable {

	private String firstName;
	private String lastName;
	private int birthYear;
	
	protected String escape(String text) {
		return text.replace("<",  "&lt;")
				.replace(">", "&gt;")
				// ..
				;
	}
	public String toXml() {
		return "<user>"
				+ "<firstName>" + escape(firstName) + "</firstName>"
				+ "<lastName>" + escape(lastName) + "</lastName>"
				+ "<birthYear>" + birthYear + "</birthYear>"
				+ "</user>";
	}
	
	
}
