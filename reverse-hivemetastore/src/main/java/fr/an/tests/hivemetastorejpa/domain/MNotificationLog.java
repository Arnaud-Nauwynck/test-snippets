package fr.an.tests.hivemetastorejpa.domain;

import java.sql.Clob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Entity
@Table(name = "NOTIFICATION_LOG",
		uniqueConstraints = @UniqueConstraint(name = "NOTIFICATION_LOG_EVENT_ID", columnNames = {"EVENT_ID"}))
@Data
public class MNotificationLog {

	@Id
	@Column(name = "NL_ID", nullable = false)
	private int nlId;

	@Column(name = "EVENT_ID", nullable = false)
	private long eventId; // This is not the datanucleus id, but the id assigned by the sequence

	@Column(name = "EVENT_TIME", nullable = false)
	private int eventTime;

	@Column(name = "EVENT_TYPE", length = 32, nullable = false)
	private String eventType;

	@Column(name = "CAT_NAME", length = 256)
	private String catalogName;

	@Column(name = "DB_NAME", length = 128)
	private String dbName;

	@Column(name = "TBL_NAME", length = 256)
	private String tableName;

	@Column(name = "MESSAGE")
	private Clob message;

	@Column(name = "MESSAGE_FORMAT", length = 16)
	private String messageFormat;

}
