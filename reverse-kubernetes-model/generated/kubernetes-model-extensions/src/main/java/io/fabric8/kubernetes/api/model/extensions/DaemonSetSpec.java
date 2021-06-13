
package io.fabric8.kubernetes.api.model.extensions;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;

@Generated("jsonschema2pojo")
public class DaemonSetSpec implements KubernetesResource
{

    public int minReadySeconds;
    public int revisionHistoryLimit;
    public LabelSelector selector;
    public PodTemplateSpec template;
    public Long templateGeneration;
    public DaemonSetUpdateStrategy updateStrategy;

}
