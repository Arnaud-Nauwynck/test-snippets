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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.an.tests.eclipselink.SampleDataJpaApplication;
import fr.an.tests.eclipselink.domain.City;

/**
 * Integration tests for {@link CityRepository}.
 *
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SampleDataJpaApplication.class)
@Transactional
public class CityRepositoryIntegrationTests {

	@Autowired
	CityRepository repository;

	@Test
	public void findsFirstPageOfCities() {
		Page<City> cities = this.repository.findAll(new PageRequest(0, 10));
		assertThat(cities.getTotalElements(), is(greaterThan(20L)));
	}

	@Test
	public void findOrCreate() {
		City res;
		List<City> cities = this.repository.findByNameAndCountry("Paris", "France");
		if (cities.isEmpty()) {
			City e = new City("Paris", "France");
			res = repository.save(e);
			System.out.println("save " + res.getId());
		} else {
			res = cities.get(0);
		}
		// find by id
		Long id = res.getId();
		System.out.println("res id:" + id);
		City res2 = repository.findOne(id);
		// Assert.assertSame(res, res2); // ??
		// redo find
		List<City> cities2 = this.repository.findByNameAndCountry("Paris", "France");
		Assert.assertSame(res, cities2.get(0));
	}
	
	
}
