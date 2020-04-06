package fr.an.tests.springbootadmincli.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BarResponse {
	public String strValue;
	public int intValue;
}