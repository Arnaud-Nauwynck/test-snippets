package fr.an.tests.hivemetastorejpa;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "WM_POOL")
@Data
public class MWMPool {

	@Id
	@Column(name = "POOL_ID", nullable = false)
	private int poolId;

	@ManyToOne
	@Column(name = "RP_ID", nullable = false)
	private MWMResourcePlan resourcePlan;

	@Column(name = "PATH", length = 1024, nullable = false)
	private String path;

	@Column(name = "ALLOC_FRACTION")
	private Double allocFraction;

	@Column(name = "QUERY_PARALLELISM")
	private Integer queryParallelism;

	@Column(name = "SCHEDULING_POLICY", length = 1024)
	private String schedulingPolicy;

	@ManyToMany
	@JoinTable(name = "WM_POOL_TO_TRIGGER", //
			joinColumns = { @JoinColumn(name = "TRIGGER_ID", referencedColumnName = "TRIGGER_ID") }, //
			inverseJoinColumns = { @JoinColumn(name = "POOL_ID", referencedColumnName = "POOL_ID") } //
			)
	private Set<MWMTrigger> triggers;

}
