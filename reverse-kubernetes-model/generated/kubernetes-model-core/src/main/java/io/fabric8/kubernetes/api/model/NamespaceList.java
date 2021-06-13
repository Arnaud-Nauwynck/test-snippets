
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class NamespaceList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.Namespace>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.Namespace> items = new ArrayList<io.fabric8.kubernetes.api.model.Namespace>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "NamespaceList";
    public ListMeta metadata;

}
