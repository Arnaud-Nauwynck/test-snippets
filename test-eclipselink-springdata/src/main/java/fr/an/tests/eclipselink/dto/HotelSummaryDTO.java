package fr.an.tests.eclipselink.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class HotelSummaryDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final MathContext MATH_CONTEXT = new MathContext(2,
			RoundingMode.HALF_UP);

	private final CityDTO city;

	private final String name;

	private final Double averageRating;

	private final Integer averageRatingRounded;

	public HotelSummaryDTO(CityDTO city, String name, Double averageRating) {
		this.city = city;
		this.name = name;
		this.averageRating = averageRating == null ? null : new BigDecimal(averageRating,
				MATH_CONTEXT).doubleValue();
		this.averageRatingRounded = averageRating == null ? null : (int) Math
				.round(averageRating);
	}

	public CityDTO getCity() {
		return this.city;
	}

	public String getName() {
		return this.name;
	}

	public Double getAverageRating() {
		return this.averageRating;
	}

	public Integer getAverageRatingRounded() {
		return this.averageRatingRounded;
	}
}
