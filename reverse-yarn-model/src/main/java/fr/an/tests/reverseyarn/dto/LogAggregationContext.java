package fr.an.tests.reverseyarn.dto;

import lombok.Data;

@Data
public class LogAggregationContext {

	  public String includePattern;
	  public String excludePattern;
	  public String rolledLogsIncludePattern;
	  public String rolledLogsExcludePattern;
	  public String logAggregationPolicyClassName;
	  public String logAggregationPolicyParameters;

}
