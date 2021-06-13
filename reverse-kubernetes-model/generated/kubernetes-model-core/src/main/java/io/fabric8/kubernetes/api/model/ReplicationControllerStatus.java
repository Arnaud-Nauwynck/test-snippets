
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ReplicationControllerStatus implements KubernetesResource
{

    public int availableReplicas;
    public List<ReplicationControllerCondition> conditions = new ArrayList<ReplicationControllerCondition>();
    public int fullyLabeledReplicas;
    public Long observedGeneration;
    public int readyReplicas;
    public int replicas;

}
