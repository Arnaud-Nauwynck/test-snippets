package fr.an.tests.webfluxservlerlog.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * to test...
 * curl -X GET -H 'Accept: application/json' http://localhost:8080/app/helloParams
 */

@Data
@AllArgsConstructor
public class LogEventDTO {

	public final String threadName;
	public final long timestamp;

	public final String logger;
	public final String severity;

	public final String message;

	public final String exception;

	public final String messageTemplate;
    public final Object[] argumentArray;
    // public final Map<String,Object> msgParams;


	public final String traceId;
	public final String userName;
	public final String traceRequest;

}