
package io.fabric8.kubernetes.api.model.policy.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class PodDisruptionBudgetList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.policy.v1.PodDisruptionBudget>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "policy/v1";
    public List<io.fabric8.kubernetes.api.model.policy.v1.PodDisruptionBudget> items = new ArrayList<io.fabric8.kubernetes.api.model.policy.v1.PodDisruptionBudget>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "PodDisruptionBudgetList";
    public ListMeta metadata;

}
