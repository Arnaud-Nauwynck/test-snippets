package fr.an.pricing.db;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class FlatTradeEntity {

	@Id
	@GeneratedValue
	private long id;
	
	String type;
	
	String currency;
	String underlying;
	double quantity;
	double strike;
	private Date expiryDate;
	
}
