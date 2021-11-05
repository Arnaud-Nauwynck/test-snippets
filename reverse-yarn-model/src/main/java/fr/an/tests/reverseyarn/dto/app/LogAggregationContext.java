package fr.an.tests.reverseyarn.dto.app;

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
