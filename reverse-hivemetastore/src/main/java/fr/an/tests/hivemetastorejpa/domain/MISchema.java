package fr.an.tests.hivemetastorejpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "I_SCHEMA")
@Data
public class MISchema {

	@Id
	@Column(name = "SCHEMA_ID")
	private int schemaId;
	
	@Column(name = "SCHEMA_TYPE", nullable = false)
	private int schemaType;
	
	@Column(name = "NAME", length = 256) // , unique,
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DB_ID")
	private MDatabase db;
	
	@Column(name = "COMPATIBILITY", nullable = false)
	private int compatibility;

	@Column(name = "VALIDATION_LEVEL", nullable = false)
	private int validationLevel;
	
	@Column(name = "CAN_EVOLVE", nullable = false)
	private boolean canEvolve;
	
	@Column(name = "SCHEMA_GROUP", length = 256)
	private String schemaGroup;
	
	@Column(name = "DESCRIPTION", length = 4000)
	private String description;

}
