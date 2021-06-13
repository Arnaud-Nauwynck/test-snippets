
package io.fabric8.kubernetes.api.model.extensions;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class ReplicaSetList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.extensions.ReplicaSet>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "extensions/v1beta1";
    public List<io.fabric8.kubernetes.api.model.extensions.ReplicaSet> items = new ArrayList<io.fabric8.kubernetes.api.model.extensions.ReplicaSet>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ReplicaSetList";
    public ListMeta metadata;

}
