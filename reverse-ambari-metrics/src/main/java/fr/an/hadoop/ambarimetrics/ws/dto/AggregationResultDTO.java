package fr.an.hadoop.ambarimetrics.ws.dto;

import java.util.Set;

import fr.an.hadoop.ambarimetrics.ws.dto.MetricAggregateDTO.MetricHostAggregateDTO;
import lombok.Data;

/**
 * see org.apache.hadoop.metrics2.sink.timeline.AggregationResult
 */
@Data
public class AggregationResultDTO {
	
    protected Set<TimelineMetricWithAggregatedValuesDTO> result;
    protected Long timeInMilis;

    /**
     * see org.apache.hadoop.metrics2.sink.timeline.TimelineMetricWithAggregatedValues
     */
    @Data
    public static class TimelineMetricWithAggregatedValuesDTO {
        private TimelineMetricDTO timelineMetric;
        private MetricHostAggregateDTO metricAggregate;
    }
}
