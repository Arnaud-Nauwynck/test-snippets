
package io.fabric8.kubernetes.api.model.autoscaling.v2beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.Quantity;

@Generated("jsonschema2pojo")
public class PodsMetricSource implements KubernetesResource
{

    public String metricName;
    public LabelSelector selector;
    public Quantity targetAverageValue;

}
