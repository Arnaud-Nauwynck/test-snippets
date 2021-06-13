
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ReplicationControllerList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.ReplicationController>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.ReplicationController> items = new ArrayList<io.fabric8.kubernetes.api.model.ReplicationController>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ReplicationControllerList";
    public ListMeta metadata;

}
