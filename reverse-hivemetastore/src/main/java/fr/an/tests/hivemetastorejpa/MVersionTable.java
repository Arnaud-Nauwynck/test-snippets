package fr.an.tests.hivemetastorejpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "VERSION")
@Data
public class MVersionTable {

	@Id
	@Column(name = "VER_ID")
	private int verId;

	@Column(name = "SCHEMA_VERSION", length = 127, nullable = false)
	private String schemaVersion;

	@Column(name = "VERSION_COMMENT", length = 255, nullable = false)
	private String versionComment;

}
