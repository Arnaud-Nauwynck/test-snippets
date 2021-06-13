
package io.fabric8.kubernetes.api.model.autoscaling.v2beta2;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class HPAScalingPolicy implements KubernetesResource
{

    public int periodSeconds;
    public String type;
    public int value;

}
