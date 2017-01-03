package fr.an.tests.eclipselink.domain;

import java.io.Serializable;

public class RatingCount implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Rating rating;

	private final long count;

	public RatingCount(Rating rating, long count) {
		this.rating = rating;
		this.count = count;
	}

	public Rating getRating() {
		return this.rating;
	}

	public long getCount() {
		return this.count;
	}
}
