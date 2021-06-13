
package io.fabric8.kubernetes.api.model.metrics.v1beta1;

import java.util.Map;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.Duration;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Quantity;

@Generated("jsonschema2pojo")
public class NodeMetrics implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String apiVersion = "metrics.k8s.io/v1beta1";
    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String kind = "NodeMetrics";
    public ObjectMeta metadata;
    public String timestamp;
    public Map<String, Quantity> usage;
    public Duration window;

}
