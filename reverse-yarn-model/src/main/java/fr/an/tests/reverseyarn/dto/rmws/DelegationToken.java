package fr.an.tests.reverseyarn.dto.rmws;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.DelegationToken
 */
@Data
public class DelegationToken {

	String token;
	String renewer;
	String owner;
	String kind;

	@XmlElement(name = "expiration-time")
	Long nextExpirationTime;

	@XmlElement(name = "max-validity")
	Long maxValidity;

}
