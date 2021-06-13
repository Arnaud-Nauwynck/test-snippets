
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PreferredSchedulingTerm implements KubernetesResource
{

    public NodeSelectorTerm preference;
    public int weight;

}
