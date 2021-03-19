package fr.an.tests.springboothttpfilter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FooResponse {
	public String strValue;
	public int intValue;
}