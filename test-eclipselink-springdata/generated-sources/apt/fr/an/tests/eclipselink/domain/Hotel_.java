package fr.an.tests.eclipselink.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Hotel.class)
public abstract class Hotel_ {

	public static volatile SingularAttribute<Hotel, String> zip;
	public static volatile SingularAttribute<Hotel, String> address;
	public static volatile SetAttribute<Hotel, Review> reviews;
	public static volatile SingularAttribute<Hotel, City> city;
	public static volatile SingularAttribute<Hotel, String> name;
	public static volatile SingularAttribute<Hotel, Long> id;

}

