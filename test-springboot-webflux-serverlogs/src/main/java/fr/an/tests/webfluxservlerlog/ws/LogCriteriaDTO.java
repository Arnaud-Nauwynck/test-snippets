package fr.an.tests.webfluxservlerlog.ws;

import java.util.Date;

public class LogCriteriaDTO {
	public String traceId;
	public String username;
	public Date fromDate;
	public String severity;
	public String msgPattern;
	public int limit;
}