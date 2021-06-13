
package io.fabric8.kubernetes.api.model.autoscaling.v2beta2;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class ContainerResourceMetricSource implements KubernetesResource
{

    public String container;
    public String name;
    public MetricTarget target;

}
