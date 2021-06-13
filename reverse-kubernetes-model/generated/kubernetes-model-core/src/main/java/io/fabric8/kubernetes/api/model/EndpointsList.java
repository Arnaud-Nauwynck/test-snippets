
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class EndpointsList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.Endpoints>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.Endpoints> items = new ArrayList<io.fabric8.kubernetes.api.model.Endpoints>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "EndpointsList";
    public ListMeta metadata;

}
