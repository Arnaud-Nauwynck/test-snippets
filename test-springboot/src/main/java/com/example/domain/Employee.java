package com.example.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@Entity
public class Employee {

	@Id
	private int id;
	
	@Version
	private int version;
	
	private String firstName, lastName, email, address;
	
	private Date birthDate;
	
	@ManyToOne
	private Department department;
	
	@OneToMany
	private List<UserProject> projects = new ArrayList<>();
	
	
	// ------------------------------------------------------------------------

	public Employee() {
	}

	// ------------------------------------------------------------------------
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	
	
}
