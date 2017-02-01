package fr.an.tests.eclipselink.dto;

import java.io.Serializable;
import java.util.Date;

public class ReviewDetailsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private RatingDTO rating;

	private Date checkInDate;

	private TripType tripType;

	private String title;

	private String details;

	public ReviewDetailsDTO() {
	}

	public RatingDTO getRating() {
		return this.rating;
	}

	public void setRating(RatingDTO rating) {
		this.rating = rating;
	}

	public Date getCheckInDate() {
		return this.checkInDate;
	}

	public void setCheckInDate(Date checkInDate) {
		this.checkInDate = checkInDate;
	}

	public TripType getTripType() {
		return this.tripType;
	}

	public void setTripType(TripType tripType) {
		this.tripType = tripType;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetails() {
		return this.details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
	
}
