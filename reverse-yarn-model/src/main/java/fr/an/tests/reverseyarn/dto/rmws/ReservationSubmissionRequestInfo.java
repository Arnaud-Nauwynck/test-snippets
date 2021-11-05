package fr.an.tests.reverseyarn.dto.rmws;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ReservationSubmissionRequestInfo
 *
 */
@Data
public class ReservationSubmissionRequestInfo {

	  private String queue;

	  @XmlElement(name = "reservation-definition")
	  private ReservationDefinitionInfo reservationDefinition;

	  @XmlElement(name = "reservation-id")
	  private String reservationId;

}
