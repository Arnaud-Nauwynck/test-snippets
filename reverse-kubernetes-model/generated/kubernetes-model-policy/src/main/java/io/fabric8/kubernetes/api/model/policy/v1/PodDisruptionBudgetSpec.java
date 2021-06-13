
package io.fabric8.kubernetes.api.model.policy.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.LabelSelector;

@Generated("jsonschema2pojo")
public class PodDisruptionBudgetSpec implements KubernetesResource
{

    public IntOrString maxUnavailable;
    public IntOrString minAvailable;
    public LabelSelector selector;

}
