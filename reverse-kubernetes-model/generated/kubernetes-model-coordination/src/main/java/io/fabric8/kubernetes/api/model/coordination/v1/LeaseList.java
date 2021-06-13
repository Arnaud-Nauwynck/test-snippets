
package io.fabric8.kubernetes.api.model.coordination.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class LeaseList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.coordination.v1.Lease>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "coordination.k8s.io/v1";
    public List<io.fabric8.kubernetes.api.model.coordination.v1.Lease> items = new ArrayList<io.fabric8.kubernetes.api.model.coordination.v1.Lease>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "LeaseList";
    public ListMeta metadata;

}
