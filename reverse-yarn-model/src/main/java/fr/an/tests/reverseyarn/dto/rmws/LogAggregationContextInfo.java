package fr.an.tests.reverseyarn.dto.rmws;

import javax.xml.bind.annotation.XmlElement;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.LogAggregationContextInfo
 */
public class LogAggregationContextInfo {

	@XmlElement(name = "log-include-pattern")
	String logIncludePattern;

	@XmlElement(name = "log-exclude-pattern")
	String logExcludePattern;

	@XmlElement(name = "rolled-log-include-pattern")
	String rolledLogsIncludePattern;

	@XmlElement(name = "rolled-log-exclude-pattern")
	String rolledLogsExcludePattern;

	@XmlElement(name = "log-aggregation-policy-class-name")
	String policyClassName;

	@XmlElement(name = "log-aggregation-policy-parameters")
	String policyParameters;

}
