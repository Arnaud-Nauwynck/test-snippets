
package io.fabric8.kubernetes.api.model.extensions;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class NetworkPolicyList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.extensions.NetworkPolicy>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "extensions/v1beta1";
    public List<io.fabric8.kubernetes.api.model.extensions.NetworkPolicy> items = new ArrayList<io.fabric8.kubernetes.api.model.extensions.NetworkPolicy>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "NetworkPolicyList";
    public ListMeta metadata;

}
