package fr.an.hadoop.ambarimetrics.ws.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import fr.an.hadoop.ambarimetrics.ws.dto.MetricAggregateDTO.MetricClusterAggregateDTO;
import fr.an.hadoop.ambarimetrics.ws.dto.MetricAggregateDTO.MetricHostAggregateDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * see org.apache.hadoop.metrics2.sink.timeline.MetricAggregate
 */
@JsonSubTypes({
	@JsonSubTypes.Type(value = MetricClusterAggregateDTO.class),
	@JsonSubTypes.Type(value = MetricHostAggregateDTO.class)
	})
@Data
public class MetricAggregateDTO {

	protected Double sum = 0.0;
	protected Double deviation;
	protected Double max = Double.MIN_VALUE;
	protected Double min = Double.MAX_VALUE;

	/**
	 * see org.apache.hadoop.metrics2.sink.timeline.MetricHostAggregate
	 * 
	 * Represents a collection of minute based aggregation of values for
	 * resolution greater than a minute.
	 */
	@Data
	@EqualsAndHashCode(callSuper=true)
	public static class MetricHostAggregateDTO extends MetricAggregateDTO {

	  private long numberOfSamples = 0;

	}

	/**
	 * see org.apache.hadoop.metrics2.sink.timeline.MetricClusterAggregate
	 */
	@Data
	@EqualsAndHashCode(callSuper=true)
	public static class MetricClusterAggregateDTO extends MetricAggregateDTO {
		
		private int numberOfHosts;
		
	}
}
