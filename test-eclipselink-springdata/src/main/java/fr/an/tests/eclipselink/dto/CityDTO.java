package fr.an.tests.eclipselink.dto;

import java.io.Serializable;

public class CityDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private String state;

	private String country;

	private String map;

	protected CityDTO() {
	}

	public CityDTO(String name, String country) {
		super();
		this.name = name;
		this.country = country;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return this.state;
	}

	public String getCountry() {
		return this.country;
	}

	public String getMap() {
		return this.map;
	}
	
	public void setState(String state) {
		this.state = state;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setMap(String map) {
		this.map = map;
	}

	
	@Override
	public String toString() {
		return getName() + "," + getState() + "," + getCountry();
	}
}
