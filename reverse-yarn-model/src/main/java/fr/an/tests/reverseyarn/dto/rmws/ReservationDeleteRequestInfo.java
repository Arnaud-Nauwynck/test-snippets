package fr.an.tests.reverseyarn.dto.rmws;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ReservationDeleteRequestInfo
 */
@Data
public class ReservationDeleteRequestInfo {

	@XmlElement(name = "reservation-id")
	private String reservationId;

	@XmlElement(name = "reservation-definition")
	private ReservationDefinitionInfo reservationDefinition;

}
