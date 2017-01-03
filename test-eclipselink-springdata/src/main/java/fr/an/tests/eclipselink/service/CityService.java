package fr.an.tests.eclipselink.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.an.tests.eclipselink.domain.City;
import fr.an.tests.eclipselink.domain.HotelSummary;

public interface CityService {

	Page<City> findCities(CitySearchCriteria criteria, Pageable pageable);

	City getCity(String name, String country);

	Page<HotelSummary> getHotels(City city, Pageable pageable);

}
