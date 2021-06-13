
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class SecretList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.Secret>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.Secret> items = new ArrayList<io.fabric8.kubernetes.api.model.Secret>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "SecretList";
    public ListMeta metadata;

}
