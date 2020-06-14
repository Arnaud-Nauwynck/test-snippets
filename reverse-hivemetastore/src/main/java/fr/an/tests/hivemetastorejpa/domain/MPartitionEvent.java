package fr.an.tests.hivemetastorejpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "PARTITION_EVENTS")
public class MPartitionEvent {

	@Id
	@Column(name = "PART_NAME_ID", nullable = false)
	private int partNameId;

	@Column(name = "CAT_NAME", length = 256)
	private String catalogName;

	@Column(name = "DB_NAME", length = 128)
	private String dbName;

	@Column(name = "TBL_NAME", length = 256)
	private String tblName;

	@Column(name = "PARTITION_NAME", length = 767)
	private String partName;

	@Column(name = "EVENT_TIME", nullable = false)
	private long eventTime;

	@Column(name = "EVENT_TYPE", nullable = false)
	private int eventType;

}
