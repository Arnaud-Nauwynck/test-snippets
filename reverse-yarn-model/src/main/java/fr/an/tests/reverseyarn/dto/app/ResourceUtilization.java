package fr.an.tests.reverseyarn.dto.app;

import lombok.Data;

@Data
public class ResourceUtilization {

	public int virtualMemory;
	public int physicalMemory;
	public float CPU;

}
