
package io.fabric8.kubernetes.api.model.discovery.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class EndpointSliceList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.discovery.v1.EndpointSlice>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "discovery.k8s.io/v1";
    public List<io.fabric8.kubernetes.api.model.discovery.v1.EndpointSlice> items = new ArrayList<io.fabric8.kubernetes.api.model.discovery.v1.EndpointSlice>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "EndpointSliceList";
    public ListMeta metadata;

}
