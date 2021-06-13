
package io.fabric8.kubernetes.api.model.autoscaling.v2beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.Quantity;

@Generated("jsonschema2pojo")
public class ContainerResourceMetricStatus implements KubernetesResource
{

    public String container;
    public int currentAverageUtilization;
    public Quantity currentAverageValue;
    public String name;

}
