
package io.fabric8.kubernetes.api.model.autoscaling.v2beta2;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class ExternalMetricStatus implements KubernetesResource
{

    public MetricValueStatus current;
    public MetricIdentifier metric;

}
