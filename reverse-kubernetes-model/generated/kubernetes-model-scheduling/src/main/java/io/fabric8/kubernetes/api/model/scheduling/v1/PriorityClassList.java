
package io.fabric8.kubernetes.api.model.scheduling.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class PriorityClassList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.scheduling.v1.PriorityClass>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "scheduling.k8s.io/v1";
    public List<io.fabric8.kubernetes.api.model.scheduling.v1.PriorityClass> items = new ArrayList<io.fabric8.kubernetes.api.model.scheduling.v1.PriorityClass>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "PriorityClassList";
    public ListMeta metadata;

}
