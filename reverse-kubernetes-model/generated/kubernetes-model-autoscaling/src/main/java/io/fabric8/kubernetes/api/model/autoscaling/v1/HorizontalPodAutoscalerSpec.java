
package io.fabric8.kubernetes.api.model.autoscaling.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class HorizontalPodAutoscalerSpec implements KubernetesResource
{

    public int maxReplicas;
    public int minReplicas;
    public CrossVersionObjectReference scaleTargetRef;
    public int targetCPUUtilizationPercentage;

}
