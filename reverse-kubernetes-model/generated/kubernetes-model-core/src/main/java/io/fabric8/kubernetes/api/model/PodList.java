
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PodList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.Pod>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.Pod> items = new ArrayList<io.fabric8.kubernetes.api.model.Pod>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "PodList";
    public ListMeta metadata;

}
