package fr.an.tests.hivemetastorejpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Represents a scheduled execution.
 */
@Entity
@Table(name = "SCHEDULED_EXECUTIONS"
)
//CONSTRAINT "SCHEDULED_EXECUTIONS_PK" PRIMARY KEY ("SCHEDULED_EXECUTION_ID"),
//CONSTRAINT "SCHEDULED_EXECUTIONS_SCHQ_FK" FOREIGN KEY ("SCHEDULED_QUERY_ID") REFERENCES "SCHEDULED_QUERIES"("SCHEDULED_QUERY_ID") ON DELETE CASCADE
@Data
public class MScheduledExecution {

	@Id
	@Column(name = "SCHEDULED_EXECUTION_ID", nullable = false)
	private int scheduledExecutionId;

	@Column(name = "SCHEDULED_QUERY_ID")
	private MScheduledQuery scheduledQuery;

	@Column(name = "EXECUTOR_QUERY_ID", length = 256)
	private String executorQueryId;

	@Column(name = "STATE", length = 256)
	private String state;

	@Column(name = "START_TIME")
	private Integer startTime;

	@Column(name = "END_TIME")
	private Integer endTime;

	@Column(name = "ERROR_MESSAGE", length = 2000)
	private String errorMessage;

	@Column(name = "LAST_UPDATE_TIME")
	private Integer lastUpdateTime;

}
