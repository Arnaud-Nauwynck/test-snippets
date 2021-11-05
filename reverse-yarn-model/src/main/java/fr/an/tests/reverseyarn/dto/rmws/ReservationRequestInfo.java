package fr.an.tests.reverseyarn.dto.rmws;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ReservationRequestInfo
 */
@Data
public class ReservationRequestInfo {

	private ResourceInfo capability;

	@XmlElement(name = "min-concurrency")
	private int minConcurrency;

	@XmlElement(name = "num-containers")
	private int numContainers;

	private long duration;

}
