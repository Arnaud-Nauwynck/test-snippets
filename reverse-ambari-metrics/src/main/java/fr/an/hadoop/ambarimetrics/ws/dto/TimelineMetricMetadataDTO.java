package fr.an.hadoop.ambarimetrics.ws.dto;

import lombok.Data;

/**
 * see org.apache.hadoop.metrics2.sink.timeline.TimelineMetricMetadata
 */
@Data
public class TimelineMetricMetadataDTO {

	private String metricName;

	private String appId;

	private String instanceId;

	private byte[] uuid;

	private String units;

	private String type = "UNDEFINED";

	public enum MetricType {
		GAUGE, COUNTER, UNDEFINED
	}

	private Long seriesStartTime;

	boolean supportsAggregates = true;

	boolean isWhitelisted = false;

	// Serialization ignored helper flag
	boolean isPersisted = false;

}
