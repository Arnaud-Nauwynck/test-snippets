package fr.an.tests.eclipselink.dto;

import java.io.Serializable;
import java.util.Set;

public class HotelDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private CityDTO city;

	private String name;

	private String address;

	private String zip;

	private Set<ReviewDTO> reviews;

	public HotelDTO() {
	}

	public HotelDTO(CityDTO city, String name) {
		this.city = city;
		this.name = name;
	}

	public CityDTO getCity() {
		return this.city;
	}

	public String getName() {
		return this.name;
	}

	public String getAddress() {
		return this.address;
	}

	public String getZip() {
		return this.zip;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<ReviewDTO> getReviews() {
		return reviews;
	}

	public void setReviews(Set<ReviewDTO> reviews) {
		this.reviews = reviews;
	}

	public void setCity(CityDTO city) {
		this.city = city;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
}
