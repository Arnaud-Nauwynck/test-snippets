package fr.an.tests.reverseyarn.dto.app;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;

@Data
public class QueueInfo {

	public String queueName;
	public float capacity;
	public float maximumCapacity;
	public float currentCapacity;
	public List<QueueInfo> childQueues;
	public List<ApplicationReport> applications;
	public QueueState queueState;
	public Set<String> labels;
	public String defaultLabelExpression;
	public QueueStatistics queueStatistics;
	public boolean preemptionDisabled;
	public Map<String, QueueConfigurations> queueConfigurations;
	public boolean intraQueuePreemptionDisabled;

}
