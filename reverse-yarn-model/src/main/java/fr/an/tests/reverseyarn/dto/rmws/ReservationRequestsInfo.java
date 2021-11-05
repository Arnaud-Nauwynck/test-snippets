package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ReservationRequestsInfo
 */
@Data
public class ReservationRequestsInfo {

	  @XmlElement(name = "reservation-request-interpreter")
	  private int reservationRequestsInterpreter;

	  @XmlElement(name = "reservation-request")
	  private ArrayList<ReservationRequestInfo> reservationRequest;

}
