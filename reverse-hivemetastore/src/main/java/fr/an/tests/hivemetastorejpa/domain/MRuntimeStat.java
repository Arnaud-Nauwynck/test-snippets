package fr.an.tests.hivemetastorejpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;

/**
 * Represents a runtime stat query entry.
 *
 * As a query may contain a large number of operatorstat entries; they are
 * stored together in a single row in the metastore. The number of operator stat
 * entries this entity has; is shown in the weight column.
 */
@Entity
@Table(name = "RUNTIME_STATS", //
		indexes = { @Index(name = "IDX_RUNTIME_STATS_CREATE_TIME", columnList = "CREATE_TIME", unique = false) })
@Data
public class MRuntimeStat {

	@Id
	@Column(name = "RS_ID")
	private int rsId;

	@Column(name = "CREATE_TIME", nullable = false)
	private int createTime;

	@Column(name = "WEIGHT", nullable = false)
	private int weight;

	@Column(name = "PAYLOAD")
	private byte[] payload;

}
