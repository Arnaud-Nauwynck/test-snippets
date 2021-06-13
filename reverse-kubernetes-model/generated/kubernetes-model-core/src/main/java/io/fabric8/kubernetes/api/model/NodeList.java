
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class NodeList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.Node>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.Node> items = new ArrayList<io.fabric8.kubernetes.api.model.Node>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "NodeList";
    public ListMeta metadata;

}
