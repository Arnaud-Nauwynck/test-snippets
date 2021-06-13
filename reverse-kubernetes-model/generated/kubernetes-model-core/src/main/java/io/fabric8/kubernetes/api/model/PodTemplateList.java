
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PodTemplateList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.PodTemplate>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.PodTemplate> items = new ArrayList<io.fabric8.kubernetes.api.model.PodTemplate>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "PodTemplateList";
    public ListMeta metadata;

}
