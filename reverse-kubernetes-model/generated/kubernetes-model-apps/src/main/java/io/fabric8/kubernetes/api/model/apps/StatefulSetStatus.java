
package io.fabric8.kubernetes.api.model.apps;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class StatefulSetStatus implements KubernetesResource
{

    public int collisionCount;
    public List<StatefulSetCondition> conditions = new ArrayList<StatefulSetCondition>();
    public int currentReplicas;
    public String currentRevision;
    public Long observedGeneration;
    public int readyReplicas;
    public int replicas;
    public String updateRevision;
    public int updatedReplicas;

}
