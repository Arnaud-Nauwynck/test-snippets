package fr.an.hadoop.ambarimetrics.ws.dto;

import java.util.HashMap;
import java.util.TreeMap;

import lombok.Data;

/**
 * see org.apache.hadoop.metrics2.sink.timeline.TimelineMetric
 */
@Data
public class TimelineMetricDTO {

	private String metricName;

	private String appId;
	
	private String instanceId;
	
	private String hostName;
	
	private long timestamp;
	
	private long startTime;
	
	private String type;
	
	private String units;
	
	private TreeMap<Long, Double> metricValues = new TreeMap<>();

	private HashMap<String, String> metadata = new HashMap<>();

}
