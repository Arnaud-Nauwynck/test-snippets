package fr.an.tests.webfluxservlerlog.dto;

public class LogCriteriaDTO {
	public String traceId;
	public String username;
	public long fromTimestamp;
	public long toTimestamp;
	public String severity;
	public String msgPattern;
	public String msgTemplatePattern;

	public int limit;
}