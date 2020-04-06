package fr.an.tests.reverseyarn.dto;

import lombok.Data;

@Data
public class ResourceUtilization {

	public int virtualMemory;
	public int physicalMemory;
	public float CPU;

}
