
package io.fabric8.kubernetes.api.model.node.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class RuntimeClassList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.node.v1beta1.RuntimeClass>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "node.k8s.io/v1beta1";
    public List<io.fabric8.kubernetes.api.model.node.v1beta1.RuntimeClass> items = new ArrayList<io.fabric8.kubernetes.api.model.node.v1beta1.RuntimeClass>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "RuntimeClassList";
    public ListMeta metadata;

}
