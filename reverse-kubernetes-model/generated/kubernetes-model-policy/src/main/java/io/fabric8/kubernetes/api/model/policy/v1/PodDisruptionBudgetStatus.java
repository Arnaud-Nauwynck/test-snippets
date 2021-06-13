
package io.fabric8.kubernetes.api.model.policy.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.Condition;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class PodDisruptionBudgetStatus implements KubernetesResource
{

    public List<Condition> conditions = new ArrayList<Condition>();
    public int currentHealthy;
    public int desiredHealthy;
    public Map<String, String> disruptedPods;
    public int disruptionsAllowed;
    public int expectedPods;
    public Long observedGeneration;

}
