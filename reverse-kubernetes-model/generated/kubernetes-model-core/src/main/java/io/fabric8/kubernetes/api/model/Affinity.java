
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Affinity implements KubernetesResource
{

    public NodeAffinity nodeAffinity;
    public PodAffinity podAffinity;
    public PodAntiAffinity podAntiAffinity;

}
