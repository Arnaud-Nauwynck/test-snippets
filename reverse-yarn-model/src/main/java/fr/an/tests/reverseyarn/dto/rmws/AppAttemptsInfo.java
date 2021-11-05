package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppAttemptsInfo
 */
public class AppAttemptsInfo {

	  @XmlElement(name = "appAttempt")
	  protected ArrayList<AppAttemptInfo> attempt = new ArrayList<AppAttemptInfo>();

}
