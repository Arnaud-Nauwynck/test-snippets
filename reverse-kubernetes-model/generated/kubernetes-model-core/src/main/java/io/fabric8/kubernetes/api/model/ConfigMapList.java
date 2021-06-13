
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ConfigMapList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.ConfigMap>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.ConfigMap> items = new ArrayList<io.fabric8.kubernetes.api.model.ConfigMap>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ConfigMapList";
    public ListMeta metadata;

}
