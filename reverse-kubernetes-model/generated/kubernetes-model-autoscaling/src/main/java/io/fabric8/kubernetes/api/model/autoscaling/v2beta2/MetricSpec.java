
package io.fabric8.kubernetes.api.model.autoscaling.v2beta2;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class MetricSpec implements KubernetesResource
{

    public ContainerResourceMetricSource containerResource;
    public ExternalMetricSource external;
    public ObjectMetricSource object;
    public PodsMetricSource pods;
    public ResourceMetricSource resource;
    public String type;

}
