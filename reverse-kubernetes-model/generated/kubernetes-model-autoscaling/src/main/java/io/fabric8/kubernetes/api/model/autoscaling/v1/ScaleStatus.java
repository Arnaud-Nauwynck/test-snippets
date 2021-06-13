
package io.fabric8.kubernetes.api.model.autoscaling.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class ScaleStatus implements KubernetesResource
{

    public int replicas;
    public String selector;

}
