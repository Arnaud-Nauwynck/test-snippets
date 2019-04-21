package fr.an.test.ambarijpa;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import fr.an.test.ambarijpa.RequestScheduleBatchRequestEntity.RequestScheduleBatchRequestEntityPK;

@IdClass(RequestScheduleBatchRequestEntityPK.class)
@Entity
@Table(name = "requestschedulebatchrequest")
@NamedQueries({ @NamedQuery(name = "findByScheduleId", query = "SELECT batchreqs FROM "
		+ "RequestScheduleBatchRequestEntity  batchreqs WHERE batchreqs.scheduleId=:id") })
public class RequestScheduleBatchRequestEntity {
	@Id
	@Column(name = "schedule_id", nullable = false, insertable = true, updatable = true)
	private Long scheduleId;

	@Id
	@Column(name = "batch_id", nullable = false, insertable = true, updatable = true)
	private Long batchId;

	public static class RequestScheduleBatchRequestEntityPK implements Serializable {
		@Id
		@Column(name = "schedule_id", nullable = false, insertable = true, updatable = true)
		private Long scheduleId;

		@Id
		@Column(name = "batch_id", nullable = false, insertable = true, updatable = true)
		private Long batchId;

	}

	@Column(name = "request_id")
	private Long requestId;

	@Column(name = "request_type", length = 255)
	private String requestType;

	@Column(name = "request_uri", length = 1024)
	private String requestUri;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "request_body")
	private byte[] requestBody;

	@Column(name = "request_status", length = 255)
	private String requestStatus;

	@Column(name = "return_code")
	private Integer returnCode;

	@Column(name = "return_message", length = 2000)
	private String returnMessage;

	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "schedule_id", referencedColumnName = "schedule_id", nullable = false, insertable = false, updatable = false) })
	private RequestScheduleEntity requestScheduleEntity;

}
