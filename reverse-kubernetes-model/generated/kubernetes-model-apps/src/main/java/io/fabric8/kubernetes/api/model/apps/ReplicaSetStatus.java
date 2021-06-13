
package io.fabric8.kubernetes.api.model.apps;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class ReplicaSetStatus implements KubernetesResource
{

    public int availableReplicas;
    public List<ReplicaSetCondition> conditions = new ArrayList<ReplicaSetCondition>();
    public int fullyLabeledReplicas;
    public Long observedGeneration;
    public int readyReplicas;
    public int replicas;

}
