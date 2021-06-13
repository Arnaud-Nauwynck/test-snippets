
package io.fabric8.kubernetes.api.model.autoscaling.v2beta2;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.Quantity;

@Generated("jsonschema2pojo")
public class MetricTarget implements KubernetesResource
{

    public int averageUtilization;
    public Quantity averageValue;
    public String type;
    public Quantity value;

}
