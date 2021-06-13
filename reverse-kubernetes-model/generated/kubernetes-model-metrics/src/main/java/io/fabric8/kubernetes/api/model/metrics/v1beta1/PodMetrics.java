
package io.fabric8.kubernetes.api.model.metrics.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.Duration;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class PodMetrics implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String apiVersion = "metrics.k8s.io/v1beta1";
    public List<ContainerMetrics> containers = new ArrayList<ContainerMetrics>();
    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String kind = "PodMetrics";
    public ObjectMeta metadata;
    public String timestamp;
    public Duration window;

}
