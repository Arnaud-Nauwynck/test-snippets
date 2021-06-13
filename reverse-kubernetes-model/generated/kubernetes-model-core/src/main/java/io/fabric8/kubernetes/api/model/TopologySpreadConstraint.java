
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class TopologySpreadConstraint implements KubernetesResource
{

    public LabelSelector labelSelector;
    public int maxSkew;
    public String topologyKey;
    public String whenUnsatisfiable;

}
