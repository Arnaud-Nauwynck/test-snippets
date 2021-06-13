
package io.fabric8.kubernetes.api.model.metrics.v1beta1;

import java.util.Map;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.Quantity;

@Generated("jsonschema2pojo")
public class ContainerMetrics implements KubernetesResource
{

    public java.lang.String name;
    public Map<String, Quantity> usage;

}
