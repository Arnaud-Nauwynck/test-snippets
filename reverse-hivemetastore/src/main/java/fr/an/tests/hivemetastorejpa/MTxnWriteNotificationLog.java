package fr.an.tests.hivemetastorejpa;

import java.sql.Clob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * MTxnWriteNotificationLog DN table for ACID write events.
 */
@Entity
@Table(name = "TXN_WRITE_NOTIFICATION_LOG")
// PRIMARY KEY ("WNL_TXNID", "WNL_DATABASE", "WNL_TABLE", "WNL_PARTITION")
@Data
public class MTxnWriteNotificationLog {

	@Id
	@Column(name = "WNL_ID", nullable = false)
	private int wnlId;

	@Id
	@Column(name = "WNL_DATABASE", length = 128, nullable = false)
	private String database;

	@Id
	@Column(name = "WNL_TABLE", length = 128, nullable = false)
	private String table;

	@Id
	@Column(name = "WNL_PARTITION", length = 767, nullable = false)
	private String partition;

	@Column(name = "WNL_TXNID", nullable = false)
	private long txnId;

	@Column(name = "WNL_WRITEID", nullable = false)
	private long writeId;

	@Column(name = "WNL_EVENT_TIME", nullable = false)
	private int eventTime;

	@Column(name = "WNL_TABLE_OBJ", nullable = false)
	private Clob tableObject;

	@Column(name = "WNL_PARTITION_OBJ")
	private Clob partObject;

	@Column(name = "WNL_FILES")
	private Clob files;

}
