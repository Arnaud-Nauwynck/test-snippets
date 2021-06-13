
package io.fabric8.kubernetes.api.model.autoscaling.v2beta2;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class HorizontalPodAutoscalerCondition implements KubernetesResource
{

    public String lastTransitionTime;
    public java.lang.String message;
    public java.lang.String reason;
    public java.lang.String status;
    public java.lang.String type;

}
