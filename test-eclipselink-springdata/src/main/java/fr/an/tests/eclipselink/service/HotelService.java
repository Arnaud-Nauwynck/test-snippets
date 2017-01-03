package fr.an.tests.eclipselink.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.an.tests.eclipselink.domain.City;
import fr.an.tests.eclipselink.domain.Hotel;
import fr.an.tests.eclipselink.domain.Review;
import fr.an.tests.eclipselink.domain.ReviewDetails;

public interface HotelService {

	Hotel getHotel(City city, String name);

	Page<Review> getReviews(Hotel hotel, Pageable pageable);

	Review getReview(Hotel hotel, int index);

	Review addReview(Hotel hotel, ReviewDetails details);

	ReviewsSummary getReviewSummary(Hotel hotel);

}
