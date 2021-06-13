
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class BaseKubernetesList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.HasMetadata>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.HasMetadata> items = new ArrayList<io.fabric8.kubernetes.api.model.HasMetadata>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "List";
    public ListMeta metadata;

}
