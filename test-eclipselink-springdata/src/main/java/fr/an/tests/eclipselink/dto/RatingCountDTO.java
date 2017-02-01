package fr.an.tests.eclipselink.dto;

import java.io.Serializable;

public class RatingCountDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private final RatingDTO rating;

	private final long count;

	public RatingCountDTO(RatingDTO rating, long count) {
		this.rating = rating;
		this.count = count;
	}

	public RatingDTO getRating() {
		return this.rating;
	}

	public long getCount() {
		return this.count;
	}
}
