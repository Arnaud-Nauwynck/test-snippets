package fr.an.tests.eclipselink.domain;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Review.class)
public abstract class Review_ {

	public static volatile SingularAttribute<Review, TripType> tripType;
	public static volatile SingularAttribute<Review, Rating> rating;
	public static volatile SingularAttribute<Review, Hotel> hotel;
	public static volatile SingularAttribute<Review, Integer> index;
	public static volatile SingularAttribute<Review, String> details;
	public static volatile SingularAttribute<Review, Long> id;
	public static volatile SingularAttribute<Review, String> title;
	public static volatile SingularAttribute<Review, Date> checkInDate;

}

