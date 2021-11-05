package fr.an.tests.reverseyarn.dto.rmws;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ApplicationSubmissionContextInfo
 */
@Data
public class ApplicationSubmissionContextInfo {

	  @XmlElement(name = "application-id")
	  String applicationId;

	  @XmlElement(name = "application-name")
	  String applicationName;

	  String queue;
	  int priority;

	  @XmlElement(name = "am-container-spec")
	  ContainerLaunchContextInfo containerInfo;

	  @XmlElement(name = "unmanaged-AM")
	  boolean isUnmanagedAM;

	  @XmlElement(name = "cancel-tokens-when-complete")
	  boolean cancelTokensWhenComplete;

	  @XmlElement(name = "max-app-attempts")
	  int maxAppAttempts;

	  @XmlElement(name = "resource")
	  ResourceInfo resource;

	  @XmlElement(name = "application-type")
	  String applicationType;

	  @XmlElement(name = "keep-containers-across-application-attempts")
	  boolean keepContainers;

	  @XmlElementWrapper(name = "application-tags")
	  @XmlElement(name = "tag")
	  Set<String> tags;
	  
	  @XmlElement(name = "app-node-label-expression")
	  String appNodeLabelExpression;
	  
	  @XmlElement(name = "am-container-node-label-expression")
	  String amContainerNodeLabelExpression;

	  @XmlElement(name = "log-aggregation-context")
	  LogAggregationContextInfo logAggregationContextInfo;

	  @XmlElement(name = "attempt-failures-validity-interval")
	  long attemptFailuresValidityInterval;

	  @XmlElement(name = "reservation-id")
	  String reservationId;

}
