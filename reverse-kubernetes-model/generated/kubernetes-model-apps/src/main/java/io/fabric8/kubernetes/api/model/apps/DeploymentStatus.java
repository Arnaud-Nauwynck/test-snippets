
package io.fabric8.kubernetes.api.model.apps;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class DeploymentStatus implements KubernetesResource
{

    public int availableReplicas;
    public int collisionCount;
    public List<DeploymentCondition> conditions = new ArrayList<DeploymentCondition>();
    public Long observedGeneration;
    public int readyReplicas;
    public int replicas;
    public int unavailableReplicas;
    public int updatedReplicas;

}
