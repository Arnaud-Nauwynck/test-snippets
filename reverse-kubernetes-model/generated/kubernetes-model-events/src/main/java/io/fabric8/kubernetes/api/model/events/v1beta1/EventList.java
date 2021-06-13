
package io.fabric8.kubernetes.api.model.events.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class EventList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.events.v1beta1.Event>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "events.k8s.io/v1beta1";
    public List<io.fabric8.kubernetes.api.model.events.v1beta1.Event> items = new ArrayList<io.fabric8.kubernetes.api.model.events.v1beta1.Event>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "EventList";
    public ListMeta metadata;

}
