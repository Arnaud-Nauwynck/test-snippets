package fr.an.tests.hivemetastorejpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "METASTORE_DB_PROPERTIES")
@Data
public class MMetastoreDBProperties {

	@Id
	@Column(name = "PROPERTY_KEY", length = 255, nullable = false)
	private String propertyKey;
	
	@Column(name = "PROPERTY_VALUE", length = 1000)
	private String propertyValue;
	
	@Column(name = "DESCRIPTION", length = 1000)
	private String description;


}
