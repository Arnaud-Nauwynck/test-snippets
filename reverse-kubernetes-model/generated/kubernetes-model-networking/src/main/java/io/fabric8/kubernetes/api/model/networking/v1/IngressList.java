
package io.fabric8.kubernetes.api.model.networking.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class IngressList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.networking.v1.Ingress>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "networking.k8s.io/v1";
    public List<io.fabric8.kubernetes.api.model.networking.v1.Ingress> items = new ArrayList<io.fabric8.kubernetes.api.model.networking.v1.Ingress>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "IngressList";
    public ListMeta metadata;

}
