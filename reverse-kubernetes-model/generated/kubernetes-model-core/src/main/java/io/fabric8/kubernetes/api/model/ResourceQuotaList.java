
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ResourceQuotaList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.ResourceQuota>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.ResourceQuota> items = new ArrayList<io.fabric8.kubernetes.api.model.ResourceQuota>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ResourceQuotaList";
    public ListMeta metadata;

}
