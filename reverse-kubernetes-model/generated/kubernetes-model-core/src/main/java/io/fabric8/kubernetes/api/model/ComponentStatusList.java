
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ComponentStatusList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.ComponentStatus>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.ComponentStatus> items = new ArrayList<io.fabric8.kubernetes.api.model.ComponentStatus>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ComponentStatusList";
    public ListMeta metadata;

}
