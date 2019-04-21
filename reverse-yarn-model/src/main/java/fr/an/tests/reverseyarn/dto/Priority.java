package fr.an.tests.reverseyarn.dto;

import lombok.Value;

@Value
public class Priority implements Comparable<Priority> {

	// public static final Priority UNDEFINED = newInstance(-1);

	public int priority;

	@Override
	public int compareTo(Priority other) {
		return other.getPriority() - this.getPriority();
	}

}
