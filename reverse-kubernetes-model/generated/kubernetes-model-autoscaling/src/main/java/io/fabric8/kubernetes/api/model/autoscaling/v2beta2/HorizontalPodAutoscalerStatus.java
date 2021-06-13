
package io.fabric8.kubernetes.api.model.autoscaling.v2beta2;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class HorizontalPodAutoscalerStatus implements KubernetesResource
{

    public List<HorizontalPodAutoscalerCondition> conditions = new ArrayList<HorizontalPodAutoscalerCondition>();
    public List<MetricStatus> currentMetrics = new ArrayList<MetricStatus>();
    public int currentReplicas;
    public int desiredReplicas;
    public String lastScaleTime;
    public Long observedGeneration;

}
