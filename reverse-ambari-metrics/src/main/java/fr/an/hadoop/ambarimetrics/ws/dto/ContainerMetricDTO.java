package fr.an.hadoop.ambarimetrics.ws.dto;

import lombok.Data;

/**
 * see org.apache.hadoop.metrics2.sink.timeline.ContainerMetric
 */
@Data
public class ContainerMetricDTO {

	private String hostName;
	private String containerId;
	private int pmemLimit;
	private int vmemLimit;
	private int pmemUsedAvg;
	private int pmemUsedMin;
	private int pmemUsedMax;
	private int pmem50Pct;
	private int pmem75Pct;
	private int pmem90Pct;
	private int pmem95Pct;
	private int pmem99Pct;
	private long launchDuration;
	private long localizationDuration;
	private long startTime;
	private long finishTime;
	private int exitCode;

}
