
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PodAffinity implements KubernetesResource
{

    public List<WeightedPodAffinityTerm> preferredDuringSchedulingIgnoredDuringExecution = new ArrayList<WeightedPodAffinityTerm>();
    public List<PodAffinityTerm> requiredDuringSchedulingIgnoredDuringExecution = new ArrayList<PodAffinityTerm>();

}
