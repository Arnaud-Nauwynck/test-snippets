
package io.fabric8.kubernetes.api.model.autoscaling.v2beta2;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class MetricStatus implements KubernetesResource
{

    public ContainerResourceMetricStatus containerResource;
    public ExternalMetricStatus external;
    public ObjectMetricStatus object;
    public PodsMetricStatus pods;
    public ResourceMetricStatus resource;
    public String type;

}
