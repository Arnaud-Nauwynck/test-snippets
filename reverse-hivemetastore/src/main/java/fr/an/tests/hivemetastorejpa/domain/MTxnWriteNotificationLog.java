package fr.an.tests.hivemetastorejpa.domain;

import java.io.Serializable;
import java.sql.Clob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import fr.an.tests.hivemetastorejpa.domain.MTxnWriteNotificationLog.MTxnWriteNotificationLogPK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MTxnWriteNotificationLog DN table for ACID write events.
 */
@Entity
@Table(name = "TXN_WRITE_NOTIFICATION_LOG")
// PRIMARY KEY ("WNL_TXNID", "WNL_DATABASE", "WNL_TABLE", "WNL_PARTITION")
@IdClass(MTxnWriteNotificationLogPK.class)
@Data
public class MTxnWriteNotificationLog {

	@Data
	@NoArgsConstructor @AllArgsConstructor
	public static class MTxnWriteNotificationLogPK implements Serializable {
		private static final long serialVersionUID = 1L;
		private int wnlId;
		private String database;
		private String table;
		private String partition;
	}
	
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
