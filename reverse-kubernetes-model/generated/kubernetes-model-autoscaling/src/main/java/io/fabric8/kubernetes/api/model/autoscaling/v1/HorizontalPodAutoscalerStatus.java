
package io.fabric8.kubernetes.api.model.autoscaling.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class HorizontalPodAutoscalerStatus implements KubernetesResource
{

    public int currentCPUUtilizationPercentage;
    public int currentReplicas;
    public int desiredReplicas;
    public String lastScaleTime;
    public Long observedGeneration;

}
