package fr.an.hadoop.ambarimetrics.ws.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * see org.apache.ambari.metrics.core.timeline.TimelineMetricServiceSummary
 */
@Data
public class TimelineMetricServiceSummaryDTO {

	private Date timestamp;
	private Map<String, String> metadataSummary = new HashMap<>();
	private Map<String, String> aggregationSummary = new HashMap<>();

}
