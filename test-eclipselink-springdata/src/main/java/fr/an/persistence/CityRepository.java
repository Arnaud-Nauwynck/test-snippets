package fr.an.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import fr.an.tests.eclipselink.domain.City;

interface CityRepository extends JpaRepository<City, Long> {

	Page<City> findByNameContainingAndCountryContainingAllIgnoringCase(String name,
			String country, Pageable pageable);

	City findByNameAndCountryAllIgnoringCase(String name, String country);

	List<City> findByNameAndCountry(String name, String country);

}
