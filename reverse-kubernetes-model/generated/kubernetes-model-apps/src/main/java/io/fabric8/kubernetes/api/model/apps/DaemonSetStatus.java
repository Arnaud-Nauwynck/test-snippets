
package io.fabric8.kubernetes.api.model.apps;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class DaemonSetStatus implements KubernetesResource
{

    public int collisionCount;
    public List<DaemonSetCondition> conditions = new ArrayList<DaemonSetCondition>();
    public int currentNumberScheduled;
    public int desiredNumberScheduled;
    public int numberAvailable;
    public int numberMisscheduled;
    public int numberReady;
    public int numberUnavailable;
    public Long observedGeneration;
    public int updatedNumberScheduled;

}
