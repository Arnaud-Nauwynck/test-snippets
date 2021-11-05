package fr.an.tests.reverseyarn.dto.rmws;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ReservationDefinitionInfo
 */
@Data
public class ReservationDefinitionInfo {

	private long arrival;

	private long deadline;

	@XmlElement(name = "reservation-requests")
	private ReservationRequestsInfo reservationRequests;

	@XmlElement(name = "reservation-name")
	private String reservationName;

	private int priority;

	@XmlElement(name = "recurrence-expression")
	private String recurrenceExpression;

}
