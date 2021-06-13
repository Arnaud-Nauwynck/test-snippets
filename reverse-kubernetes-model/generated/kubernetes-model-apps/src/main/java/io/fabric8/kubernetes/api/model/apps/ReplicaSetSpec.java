
package io.fabric8.kubernetes.api.model.apps;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;

@Generated("jsonschema2pojo")
public class ReplicaSetSpec implements KubernetesResource
{

    public int minReadySeconds;
    public int replicas;
    public LabelSelector selector;
    public PodTemplateSpec template;

}
