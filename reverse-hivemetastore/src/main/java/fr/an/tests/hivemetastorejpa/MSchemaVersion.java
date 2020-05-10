package fr.an.tests.hivemetastorejpa;

import java.sql.Clob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "SCHEMA_VERSION")
// unique ("SCHEMA_ID", "VERSION")
@Data
public class MSchemaVersion {

	@Id
	@Column(name = "SCHEMA_VERSION_ID")
	private int schemaVersionId;

	@ManyToOne
	@JoinColumn(name = "SCHEMA_ID")
	private MISchema iSchema;

	@Column(name = "VERSION", nullable = false)
	private int version;

	@Column(name = "CREATED_AT", nullable = false)
	private long createdAt;

	@ManyToOne
	@JoinColumn(name = "CD_ID")
	private MColumnDescriptor cols;

	@Column(name = "STATE", nullable = false)
	private int state;

	@Column(name = "DESCRIPTION", length = 4000)
	private String description;

	@Column(name = "SCHEMA_TEXT")
	private Clob schemaText;

	@Column(name = "FINGERPRINT", length = 256)
	private String fingerprint;

	@Column(name = "SCHEMA_VERSION_NAME", length = 256)
	private String name;

	@ManyToOne
	@JoinColumn(name = "SERDE_ID")
	private MSerDeInfo serDe;

}
