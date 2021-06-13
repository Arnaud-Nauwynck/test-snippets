
package io.fabric8.kubernetes.api.model.metrics.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class NodeMetricsList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "metrics.k8s.io/v1beta1";
    public List<io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics> items = new ArrayList<io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "NodeMetricsList";
    public ListMeta metadata;

}
