
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class EventList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.Event>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.Event> items = new ArrayList<io.fabric8.kubernetes.api.model.Event>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "EventList";
    public ListMeta metadata;

}
