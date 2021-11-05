package fr.an.tests.reverseyarn.dto.rmws;

import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;

import fr.an.tests.reverseyarn.dto.app.ApplicationAccessType;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ContainerLaunchContextInfo
 */
public class ContainerLaunchContextInfo {


	  @XmlElementWrapper(name = "local-resources")
	  HashMap<String, LocalResourceInfo> local_resources;

	  HashMap<String, String> environment;

	  List<String> commands;

	  @XmlElementWrapper(name = "service-data")
	  HashMap<String, String> servicedata;

	  CredentialsInfo credentials;

	  @XmlElementWrapper(name = "application-acls")
	  HashMap<ApplicationAccessType, String> acls;

}
