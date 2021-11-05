package fr.an.tests.reverseyarn.dto.rmws;

import javax.xml.bind.annotation.XmlElement;

import fr.an.tests.reverseyarn.dto.app.ApplicationTimeoutType;
import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppTimeoutInfo
 */
@Data
public class AppTimeoutInfo {

	@XmlElement(name = "type")
	private ApplicationTimeoutType timeoutType;

	private String expiryTime;

	@XmlElement(name = "remainingTimeInSeconds")
	private long remainingTimeInSec;

}
