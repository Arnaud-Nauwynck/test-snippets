
package io.fabric8.kubernetes.api.model.autoscaling.v2beta2;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class ObjectMetricSource implements KubernetesResource
{

    public CrossVersionObjectReference describedObject;
    public MetricIdentifier metric;
    public MetricTarget target;

}
