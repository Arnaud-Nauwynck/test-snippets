
package io.fabric8.kubernetes.api.model.extensions;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;

@Generated("jsonschema2pojo")
public class DeploymentSpec implements KubernetesResource
{

    public int minReadySeconds;
    public boolean paused;
    public int progressDeadlineSeconds;
    public int replicas;
    public int revisionHistoryLimit;
    public RollbackConfig rollbackTo;
    public LabelSelector selector;
    public DeploymentStrategy strategy;
    public PodTemplateSpec template;

}
