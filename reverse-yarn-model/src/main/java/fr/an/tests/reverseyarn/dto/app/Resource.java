package fr.an.tests.reverseyarn.dto.app;

import lombok.Data;

@Data
public class Resource implements Comparable<Resource> {

	public int memory;
	public int virtualCores;

	@Override
	public int compareTo(Resource other) {
		int diff = this.getMemory() - other.getMemory();
		if (diff == 0) {
			diff = this.getVirtualCores() - other.getVirtualCores();
		}
		return diff;
	}

}
