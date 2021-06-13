
package io.fabric8.kubernetes.api.model.networking.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class IngressClassList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.networking.v1.IngressClass>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "networking.k8s.io/v1";
    public List<io.fabric8.kubernetes.api.model.networking.v1.IngressClass> items = new ArrayList<io.fabric8.kubernetes.api.model.networking.v1.IngressClass>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "IngressClassList";
    public ListMeta metadata;

}
