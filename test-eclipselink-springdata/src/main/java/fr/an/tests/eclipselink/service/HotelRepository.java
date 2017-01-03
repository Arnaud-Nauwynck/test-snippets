package fr.an.tests.eclipselink.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import fr.an.tests.eclipselink.domain.City;
import fr.an.tests.eclipselink.domain.Hotel;
import fr.an.tests.eclipselink.domain.HotelSummary;
import fr.an.tests.eclipselink.domain.RatingCount;

interface HotelRepository extends Repository<Hotel, Long> {

	Hotel findByCityAndName(City city, String name);

	@Query("select new fr.an.tests.eclipselink.domain.HotelSummary(h.city, h.name, avg(r.rating)) "
			+ "from Hotel h left outer join h.reviews r where h.city = ?1 group by h")
	Page<HotelSummary> findByCity(City city, Pageable pageable);

	@Query("select new fr.an.tests.eclipselink.domain.RatingCount(r.rating, count(r)) "
			+ "from Review r where r.hotel = ?1 group by r.rating order by r.rating DESC")
	List<RatingCount> findRatingCounts(Hotel hotel);
}
