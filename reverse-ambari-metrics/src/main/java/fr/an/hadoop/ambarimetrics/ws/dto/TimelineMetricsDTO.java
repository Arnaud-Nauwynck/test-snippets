package fr.an.hadoop.ambarimetrics.ws.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;


/**
 * see org.apache.hadoop.metrics2.sink.timeline.TimelineMetrics
 */
@Data
public class TimelineMetricsDTO {

	private List<TimelineMetricDTO> metrics = new ArrayList<TimelineMetricDTO>();

}
