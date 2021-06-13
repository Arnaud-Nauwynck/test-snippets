
package io.fabric8.kubernetes.api.model.autoscaling.v2beta2;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class HPAScalingRules implements KubernetesResource
{

    public List<HPAScalingPolicy> policies = new ArrayList<HPAScalingPolicy>();
    public String selectPolicy;
    public int stabilizationWindowSeconds;

}
