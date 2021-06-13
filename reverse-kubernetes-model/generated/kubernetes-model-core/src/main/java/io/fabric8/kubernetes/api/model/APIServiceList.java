
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class APIServiceList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.APIService>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "apiregistration.k8s.io/v1";
    public List<io.fabric8.kubernetes.api.model.APIService> items = new ArrayList<io.fabric8.kubernetes.api.model.APIService>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "APIServiceList";
    public ListMeta metadata;

}
