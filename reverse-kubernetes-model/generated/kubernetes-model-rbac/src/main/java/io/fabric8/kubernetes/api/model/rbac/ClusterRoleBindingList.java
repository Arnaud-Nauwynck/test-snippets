
package io.fabric8.kubernetes.api.model.rbac;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class ClusterRoleBindingList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.rbac.ClusterRoleBinding>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "rbac.authorization.k8s.io/v1";
    public List<io.fabric8.kubernetes.api.model.rbac.ClusterRoleBinding> items = new ArrayList<io.fabric8.kubernetes.api.model.rbac.ClusterRoleBinding>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ClusterRoleBindingList";
    public ListMeta metadata;

}
