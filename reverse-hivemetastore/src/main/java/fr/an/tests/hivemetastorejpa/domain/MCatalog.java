package fr.an.tests.hivemetastorejpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "CTLGS")
public class MCatalog {

	@Id
	@Column(name = "CTLG_ID")
	private int id;

	@Column(name = "NAME", length = 256, unique = true)
	private String name;

	@Column(name = "DESC", length = 4000)
	private String description;

	@Column(name = "LOCATION_URI", length = 4000, nullable = false)
	private String locationUri;

	@Column(name = "CREATE_TIME")
	private int createTime;

}
