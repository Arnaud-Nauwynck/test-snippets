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
 * Describes a scheduled query.
 */
@Entity
@Table(name = "SCHEDULED_QUERIES")
// CONSTRAINT "SCHEDULED_QUERIES_PK" PRIMARY KEY ("SCHEDULED_QUERY_ID")
@Data
public class MScheduledQuery {

	@Id
	@Column(name = "SCHEDULED_QUERY_ID", nullable = false)
	private int scheduledQueryId;

	@Column(name = "CLUSTER_NAMESPACE", length = 256)
	private String clusterNamespace;

	@Column(name = "SCHEDULE_NAME", length = 256)
	private String scheduleName;

	@Column(name = "ENABLED", nullable = false)
	private boolean enabled;

	@Column(name = "SCHEDULE", length = 256)
	private String schedule;

	@Column(name = "USER", length = 256)
	private String user;

	@Column(name = "QUERY", length = 4000)
	private String query;

	@Column(name = "NEXT_EXECUTION")
	private Integer nextExecution;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACTIVE_EXECUTION_ID")
	private MScheduledExecution activeExecution;

	@OneToMany(mappedBy = "scheduledQuery")
	private Set<MScheduledExecution> executions;

}
