
package io.fabric8.kubernetes.api.model.batch.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;

@Generated("jsonschema2pojo")
public class JobSpec implements KubernetesResource
{

    public Long activeDeadlineSeconds;
    public int backoffLimit;
    public String completionMode;
    public int completions;
    public boolean manualSelector;
    public int parallelism;
    public LabelSelector selector;
    public boolean suspend;
    public PodTemplateSpec template;
    public int ttlSecondsAfterFinished;

}
