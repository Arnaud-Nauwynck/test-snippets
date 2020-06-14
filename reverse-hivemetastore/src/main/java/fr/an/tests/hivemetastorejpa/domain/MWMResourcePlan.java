package fr.an.tests.hivemetastorejpa.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

/**
 * Storage class for ResourcePlan.
 */
@Entity
@Table(name = "WM_RESOURCEPLAN")
@Data
public class MWMResourcePlan {

	@Id
	@Column(name = "RP_ID", nullable = false)
	private int rpId;

	@Column(name = "NAME", length = 128, nullable = false)
	private String name;

	@Column(name = "NS", length = 128)
	private String ns;

	@Column(name = "QUERY_PARALLELISM")
	private Integer queryParallelism;

	public enum Status {
		ACTIVE, ENABLED, DISABLED
	}

	@Column(name = "STATUS", length = 20, nullable = false)
	private Status status;

	@OneToMany(mappedBy = "resourcePlan")
	private Set<MWMPool> pools;

	@OneToMany(mappedBy = "resourcePlan")
	private Set<MWMTrigger> triggers;

	@OneToMany(mappedBy = "resourcePlan")
	private Set<MWMMapping> mappings;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEFAULT_POOL_ID")
	private MWMPool defaultPool;

}
