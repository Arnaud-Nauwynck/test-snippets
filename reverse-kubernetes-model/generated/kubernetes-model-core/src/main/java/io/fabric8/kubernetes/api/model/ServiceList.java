
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ServiceList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.Service>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.Service> items = new ArrayList<io.fabric8.kubernetes.api.model.Service>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ServiceList";
    public ListMeta metadata;

}
