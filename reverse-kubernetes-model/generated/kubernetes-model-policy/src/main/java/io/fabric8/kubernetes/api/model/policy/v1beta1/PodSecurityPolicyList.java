
package io.fabric8.kubernetes.api.model.policy.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class PodSecurityPolicyList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.policy.v1beta1.PodSecurityPolicy>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "policy/v1beta1";
    public List<io.fabric8.kubernetes.api.model.policy.v1beta1.PodSecurityPolicy> items = new ArrayList<io.fabric8.kubernetes.api.model.policy.v1beta1.PodSecurityPolicy>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "PodSecurityPolicyList";
    public ListMeta metadata;

}
