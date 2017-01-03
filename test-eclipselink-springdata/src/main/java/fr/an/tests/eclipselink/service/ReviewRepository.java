package fr.an.tests.eclipselink.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import fr.an.tests.eclipselink.domain.Hotel;
import fr.an.tests.eclipselink.domain.Review;

interface ReviewRepository extends Repository<Review, Long> {

	Page<Review> findByHotel(Hotel hotel, Pageable pageable);

	Review findByHotelAndIndex(Hotel hotel, int index);

	Review save(Review review);

}
