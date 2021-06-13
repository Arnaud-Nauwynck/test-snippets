
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ServiceAccountList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.ServiceAccount>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.ServiceAccount> items = new ArrayList<io.fabric8.kubernetes.api.model.ServiceAccount>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ServiceAccountList";
    public ListMeta metadata;

}
