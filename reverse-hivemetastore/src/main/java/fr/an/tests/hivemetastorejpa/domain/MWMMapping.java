package fr.an.tests.hivemetastorejpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "WM_MAPPING")
@Data
public class MWMMapping {

	@Id
	@Column(name = "MAPPING_ID", nullable = false)
	private int mappingId;

	@ManyToOne
	@JoinColumn(name = "RP_ID", nullable = false)
	private MWMResourcePlan resourcePlan;

	public enum EntityType {
		USER, GROUP, APPLICATION
	}

	@Column(name = "ENTITY_TYPE", length = 128, nullable = false)
	private EntityType entityType;

	@Column(name = "ENTITY_NAME", length = 128, nullable = false)
	private String entityName;

	@ManyToOne
	@JoinColumn(name = "POOL_ID")
	private MWMPool pool;

	@Column(name = "ORDERING")
	private Integer ordering;

}
