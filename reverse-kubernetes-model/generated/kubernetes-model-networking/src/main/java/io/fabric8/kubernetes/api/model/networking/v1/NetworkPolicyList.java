
package io.fabric8.kubernetes.api.model.networking.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class NetworkPolicyList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicy>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "networking.k8s.io/v1";
    public List<io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicy> items = new ArrayList<io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicy>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "NetworkPolicyList";
    public ListMeta metadata;

}
