package fr.an.tests.reverseyarn.dto.rmws;

public enum SchedulerResourceTypes {
	MEMORY(0),
    CPU(1);

	public final int value;
	
	private SchedulerResourceTypes(int value) {
		this.value = value;
	}

}
