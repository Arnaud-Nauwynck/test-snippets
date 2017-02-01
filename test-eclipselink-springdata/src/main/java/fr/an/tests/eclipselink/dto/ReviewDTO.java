package fr.an.tests.eclipselink.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.util.Assert;

public class ReviewDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private HotelDTO hotel;

	private int index;

	private RatingDTO rating;

	private Date checkInDate;

	private TripType tripType;

	private String title;

	private String details;

	protected ReviewDTO() {
	}

	public ReviewDTO(HotelDTO hotel, int index, ReviewDetailsDTO details) {
		Assert.notNull(hotel, "Hotel must not be null");
		Assert.notNull(details, "Details must not be null");
		this.hotel = hotel;
		this.index = index;
		this.rating = details.getRating();
		this.checkInDate = details.getCheckInDate();
		this.tripType = details.getTripType();
		this.title = details.getTitle();
		this.details = details.getDetails();
	}

	public HotelDTO getHotel() {
		return this.hotel;
	}

	public int getIndex() {
		return this.index;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setHotel(HotelDTO hotel) {
		this.hotel = hotel;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
}
