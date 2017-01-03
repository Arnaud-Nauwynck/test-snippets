/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.an.tests.eclipselink.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.an.tests.eclipselink.SampleDataJpaApplication;
import fr.an.tests.eclipselink.domain.City;
import fr.an.tests.eclipselink.domain.Hotel;
import fr.an.tests.eclipselink.domain.HotelSummary;
import fr.an.tests.eclipselink.domain.Rating;
import fr.an.tests.eclipselink.domain.RatingCount;
import fr.an.tests.eclipselink.service.CityRepository;
import fr.an.tests.eclipselink.service.HotelRepository;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Integration tests for {@link HotelRepository}.
 *
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SampleDataJpaApplication.class)
public class HotelRepositoryIntegrationTests {

	@Autowired
	CityRepository cityRepository;
	@Autowired
	HotelRepository repository;

	@Test
	public void executesQueryMethodsCorrectly() {
		City city = this.cityRepository
				.findAll(new PageRequest(0, 1, Direction.ASC, "name")).getContent()
				.get(0);
		assertThat(city.getName(), is("Atlanta"));

		Page<HotelSummary> hotels = this.repository.findByCity(city, new PageRequest(0,
				10, Direction.ASC, "name"));
		Hotel hotel = this.repository.findByCityAndName(city, hotels.getContent().get(0)
				.getName());
		assertThat(hotel.getName(), is("Doubletree"));

		List<RatingCount> counts = this.repository.findRatingCounts(hotel);
		assertThat(counts, hasSize(1));
		assertThat(counts.get(0).getRating(), is(Rating.AVERAGE));
		assertThat(counts.get(0).getCount(), is(greaterThan(1L)));
	}
}
