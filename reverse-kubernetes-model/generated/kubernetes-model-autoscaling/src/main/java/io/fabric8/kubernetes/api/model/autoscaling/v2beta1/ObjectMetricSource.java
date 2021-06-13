
package io.fabric8.kubernetes.api.model.autoscaling.v2beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.Quantity;

@Generated("jsonschema2pojo")
public class ObjectMetricSource implements KubernetesResource
{

    public Quantity averageValue;
    public String metricName;
    public LabelSelector selector;
    public CrossVersionObjectReference target;
    public Quantity targetValue;

}
