package fr.an.tests.eclipselink.service;

import fr.an.tests.eclipselink.domain.Rating;

public interface ReviewsSummary {

	long getNumberOfReviewsWithRating(Rating rating);

}
